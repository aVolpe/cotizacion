/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API: string
  readonly VITE_SWAGGER: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
