interface Token {
  accessToken: string;
  refreshToken: string;
  grantType: string;
  tokenExpiresIn: number;
  username: string;
}

declare module "vuex" {
  export * from "vuex/types/index.d.ts";
  export * from "vuex/types/helpers.d.ts";
  export * from "vuex/types/logger.d.ts";
  export * from "vuex/types/vue.d.ts";
}

type ElementType<T> = T extends (infer U)[] ? U : never

