// ESLint flat config for the studio React + TypeScript + Vite project.
// This enforces consistent code style and catches common bugs early.

import tsParser from '@typescript-eslint/parser';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import eslintConfigPrettier from 'eslint-config-prettier';

/** @type {import('eslint').Linter.FlatConfig[]} */
export default [
  {
    // Ignore build artifacts and external directories from linting.
    ignores: ['dist/**', 'node_modules/**', 'coverage/**', '.next/**'],
  },
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    settings: {
      react: {
        // Automatically detect the installed React version.
        version: 'detect',
      },
    },
    plugins: {
      '@typescript-eslint': tsPlugin,
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
    },
    rules: {
      // Start from recommended rule-sets for TypeScript and React.
      ...tsPlugin.configs.recommended.rules,
      ...reactPlugin.configs.recommended.rules,
      ...reactHooksPlugin.configs.recommended.rules,

      // Disable formatting-related rules in favor of Prettier.
      ...eslintConfigPrettier.rules,

      // React 17+ with new JSX transform does not require React in scope.
      'react/react-in-jsx-scope': 'off',
      // We typically rely on TypeScript types instead of PropTypes.
      'react/prop-types': 'off',

      // Encourage explicit unused handling but avoid noisy false positives.
      '@typescript-eslint/no-unused-vars': [
        'warn',
        { argsIgnorePattern: '^_', varsIgnorePattern: '^_' },
      ],

      // Relax a few strict rules that do not yet match the current codebase.
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-require-imports': 'off',

      // Keep console usage intentional (still allowed during development).
      'no-console': 'warn',
    },
  },
];


