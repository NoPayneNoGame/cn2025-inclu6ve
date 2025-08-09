module.exports = {
  root: true,
  env: { node: true },
  "extends": [
    "plugin:vue/vue3-essential",
    "eslint:recommended",
    "@vue/typescript/recommended",
  ],
  plugins: ["import"],
  parserOptions: {
    ecmaVersion: 2020,
    warnOnUnsupportedTypeScriptVersion: false,
  },
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-restricted-imports": ["error", { patterns: [".*"] }],
    "arrow-parens": ["error", "always"],
    "comma-dangle": ["error", "always-multiline"],
    "semi": ["error", "always", { omitLastInOneLineBlock: true }],
    "quotes": ["error", "double", { avoidEscape: true }],
    "import/order": [
      "error",
      {
        groups: [
          ["builtin", "external"],
          "internal",
          ["sibling", "parent"], // Relative imports, sibling and parent types they can be mingled together
          "index",
          "unknown",
        ],
        "newlines-between": "always",
        alphabetize: {
          order: "asc",
          orderImportKind: "asc",
          caseInsensitive: true,
        },
      },
    ],
    "vue/block-order": ["error", { order: [ "template", "script", "style" ] }],
    "vue/component-name-in-template-casing": ["error", "kebab-case"],
    "vue/html-closing-bracket-newline": ["error", { multiline: "always", singleline: "never" }],
    "vue/no-deprecated-slot-attribute": "off",
    "vue/script-indent": ["error", 2, { baseIndent: 1, switchCase: 1 }],
    "@typescript-eslint/consistent-type-imports": "error",
    "@typescript-eslint/no-empty-interface": ["error", { allowSingleExtends: true }],
    "@typescript-eslint/no-namespace": "warn",
  },
};
