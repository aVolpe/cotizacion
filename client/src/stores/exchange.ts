import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type {
  Branch,
  ExchangeDataDTO,
  Place,
  QueryResponseDetail,
  SingleExchangeData
} from '@/api/ExchangeAPI'
import { ExchangeAPI, Type } from '@/api/ExchangeAPI'
import router from '../router'

const getGmap = (branch: Branch) => {
  if (branch && branch.latitude)
    return `https://www.google.com/maps/search/?api=1&query=${branch.latitude},${branch.longitude}`
  else
    return null
}

export interface Loaded<T> {
  loading: boolean
  loaded: boolean
  data?: T
}

function addMinutes(date: Date, minutes: number): Date {
  return new Date(date.getTime() + minutes * 60000)
}

export const useExchangeStore = defineStore('exchange', () => {
  // State
  const currencies = ref<Loaded<string[]>>({
    loading: false,
    loaded: false
  })

  const branches = ref<{ [k: string]: Loaded<Branch> }>({})

  const exchanges = ref<{ [k: string]: Loaded<ExchangeDataDTO> }>({})

  const current = ref({
    currency: 'USD',
    amount: 1,
    place: undefined as number | undefined
  })

  const exchangeDialog = ref({
    loading: false,
    show: false,
    data: undefined as SingleExchangeData | undefined
  })

  // Getters
  const currentCurrencyData = computed((): Loaded<ExchangeDataDTO> => {
    if (!current.value.currency || !exchanges.value[current.value.currency])
      return { loading: false, loaded: false }
    return exchanges.value[current.value.currency]
  })

  const filteredCurrencies = computed((): Loaded<ExchangeDataDTO> => {
    if (!current.value.currency || !exchanges.value[current.value.currency])
      return { loading: false, loaded: false }
    const resource = exchanges.value[current.value.currency]

    const filteredPlace = current.value.place
    if (!filteredPlace || !resource.data)
      return resource

    return {
      ...resource,
      data: {
        ...resource.data,
        data: resource.data.data.filter((l: QueryResponseDetail) => l.place.id === filteredPlace)
      }
    }
  })

  const currentPlaces = computed((): Place[] => {
    if (!current.value.currency || !exchanges.value[current.value.currency])
      return []
    const resource = exchanges.value[current.value.currency]

    if (!resource.data)
      return []

    const uniq: { [k: number]: Place } = {}
    resource.data.data.map(l => l.place)
      .forEach(l => uniq[l.id] = l)
    return Object.values(uniq)
      .sort((i1, i2) => i1.name.localeCompare(i2.name))
  })

  const branchesWithLocation = computed((): Loaded<ExchangeDataDTO> => {
    if (!current.value.currency || !exchanges.value[current.value.currency])
      return { loading: false, loaded: false }
    const baseData = exchanges.value[current.value.currency]
    if (!baseData.loaded) {
      return baseData
    }
    return {
      ...baseData,
      data: {
        ...baseData.data!,
        data: baseData.data!.data.filter(qr => qr.branch && qr.branch.gmaps)
      }
    }
  })

  // Actions
  function hideExchangeDialog() {
    exchangeDialog.value = {
      loading: false,
      show: false,
      data: undefined
    }
  }

  function setDialogData(query: QueryResponseDetail) {
    exchangeDialog.value = {
      show: true,
      loading: false,
      data: {
        place: query.place,
        branch: query.branch,
        exchange: {
          purchasePrice: query.purchasePrice,
          salePrice: query.salePrice,
          currency: current.value.currency,
          date: query.queryDate
        }
      }
    }
  }

  async function showExchangeData(query: QueryResponseDetail, force: boolean = false) {
    if (query.place.type !== Type.Bank) {
      setDialogData(query)
      return
    }

    if (!force && branches.value[query.place.code] && branches.value[query.place.code].loaded) {
      setDialogData({
        ...query,
        place: {
          ...query.place,
          branches: branches.value[query.place.code].data
        }
      })
    } else {
      const data = await ExchangeAPI.getBranches(query.place.code)
      data.forEach(d => d.gmaps = getGmap(d))
      query.place.branches = data
      branches.value = {
        ...branches.value,
        [query.place.code]: {
          loaded: true,
          loading: false,
          data
        }
      }
      setDialogData({
        ...query,
        place: {
          ...query.place,
          branches: data
        }
      })
    }
  }

  function setCurrentCurrency(currency: string) {
    router.replace({
      query: {
        moneda: currency
      }
    })
    current.value.currency = currency
    fetchExchange(currency)
  }

  function setAmount(newAmount: number) {
    current.value.amount = newAmount
  }

  function setCurrentPlace(placeId?: number) {
    current.value.place = placeId && placeId > 0 ? placeId : undefined
  }

  function clearCurrentPlace() {
    current.value.place = undefined
  }

  async function init() {
    fetchCurrencies()
    fetchExchange('USD')
    currencies.value = {
      loading: false,
      loaded: true,
      data: ['USD']
    }
  }

  async function fetchCurrencies() {
    currencies.value.loading = true
    const data = await ExchangeAPI.getCurrencies()
    currencies.value = {
      loading: false,
      loaded: true,
      data
    }
  }

  async function fetchCurrencyData(currency: string = 'USD') {
    if (current.value.currency !== currency) {
      current.value.currency = currency
    }
    const exchangesValue = exchanges.value

    if (currency in exchangesValue &&
      exchangesValue[currency].loaded &&
      new Date(exchangesValue[currency].data!.lastQueryResult) > addMinutes(new Date(), -15)
    ) {
      console.debug("Don't reload date because is was fetched recently")
    } else {
      fetchExchange(currency)
    }
  }

  async function fetchExchange(isoCode: string, force: boolean = false) {
    if (!force && exchanges.value[isoCode]) {
      return
    }
    exchanges.value = {
      ...exchanges.value,
      [isoCode]: {
        loading: true,
        loaded: false
      }
    }

    const result: ExchangeDataDTO = await ExchangeAPI.getTodayExchange(isoCode)

    if (result === null) return

    const toRet: QueryResponseDetail[] = []

    for (const row of result.data) {
      if (row.branch) {
        row.branch.place = row.place
        row.branch.gmaps = getGmap(row.branch)
      }
      toRet.push(row)
    }

    if (current.value.place) {
      const countOfCurrentPlace = toRet.filter(tr => tr.place.id === current.value.place).length
      if (countOfCurrentPlace === 0) {
        clearCurrentPlace()
      }
    }

    exchanges.value = {
      ...exchanges.value,
      [isoCode]: {
        loading: false,
        loaded: true,
        data: {
          ...result,
          lastQueryResult: result.lastQueryResult,
          firstQueryResult: result.firstQueryResult,
          count: result.count,
          data: toRet
        }
      }
    }
  }

  return {
    // State
    currencies,
    branches,
    exchanges,
    current,
    exchangeDialog,
    // Getters
    currentCurrencyData,
    filteredCurrencies,
    currentPlaces,
    branchesWithLocation,
    // Actions
    hideExchangeDialog,
    setDialogData,
    showExchangeData,
    setCurrentCurrency,
    setAmount,
    setCurrentPlace,
    clearCurrentPlace,
    init,
    fetchCurrencies,
    fetchCurrencyData,
    fetchExchange
  }
})
