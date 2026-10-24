// Kill switch for the Workbox service worker that @vue/cli-plugin-pwa installed
// before the Vite migration. Browsers that still have it registered fetch this
// URL on update, swap to this worker, which clears the old caches, unregisters
// itself and reloads open tabs so they load the current app from the network.
self.addEventListener('install', () => self.skipWaiting())

self.addEventListener('activate', (event) => {
  event.waitUntil((async () => {
    const keys = await caches.keys()
    await Promise.all(keys.map((key) => caches.delete(key)))
    await self.registration.unregister()
    const clients = await self.clients.matchAll({ type: 'window', includeUncontrolled: true })
    clients.forEach((client) => client.navigate(client.url))
  })())
})
