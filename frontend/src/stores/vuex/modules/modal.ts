import { Module } from 'vuex'
import { VuexModules } from '../index.ts'
import { VNode } from 'vue'


export interface ModalModuleState {
  isOpen: boolean
  content: VNode | null
  onClose: OnClose
  closeText: string
  onConfirm: OnConfirm
  confirmText: string
  placeholder: string
  inputValue: InputValue
  modalType: ModalType
  selectBoxValue: SelectOption
  isVacation: boolean,
  inputHandler: InputHandler
  disableConfirm: boolean
}


export const selectOptions = ['forest', 'red', 'black', 'skyblue', 'orange', 'purple'] as const
export type SelectOption = ElementType<typeof selectOptions>
export type InputValue = string
export type OnConfirm = (() => void) | ((inputValue: InputValue) => void) | null
export type OnClose = (() => void) | null
export type InputHandler = ((input: string, prevInput: string) => string) | null

export const modalTypes = ['alert', 'input'] as const

export type ModalType = ElementType<typeof modalTypes>


const modalModule: Module<ModalModuleState, VuexModules> = {
  state() {
    return {
      isOpen: false,
      content: null,
      onClose: null,
      onConfirm: null,
      inputValue: '',
      placeholder: '',
      modalType: 'alert', // default
      selectBoxValue: 'green',
      isVacation: false,
      closeText: '닫기',
      confirmText: '확인',
      inputHandler: null,
      disableConfirm: false
    }
  },
  actions: {},
  mutations: {
    setModal(state, {
      isOpen,
      content,
      onClose,
      onConfirm,
      modalType,
      placeholder,
      inputValue,
      selectBoxValue,
      isVacation,
      confirmText,
      closeText,
      disableConfirm,
      inputHandler
    }: ModalModuleState) {
      state.content = content ?? null
      state.isOpen = isOpen ?? false
      state.onClose = onClose ?? null
      state.onConfirm = onConfirm ?? null
      state.modalType = modalType ?? 'alert'
      state.placeholder = placeholder ?? ''
      state.inputValue = inputValue ?? ''
      state.selectBoxValue = selectBoxValue ?? 'green'
      state.isVacation = isVacation ?? false
      state.confirmText = confirmText ?? '확인'
      state.closeText = closeText ?? '닫기'
      state.inputHandler = inputHandler ?? null
      state.disableConfirm = disableConfirm ?? false
    },
    setInputValue(state, payload) {
      if(state.inputHandler) {
        const newInputValue = state.inputHandler(payload, state.inputValue)
        state.inputValue = newInputValue
      } else {
        state.inputValue = payload
      }
    },
    setSelectBoxValue(state, payload) {
      state.selectBoxValue = payload
    },
    setIsVacation(state, payload) {
      state.isVacation = payload
    },
    setPlaceholder(state, payload) {
      state.placeholder = payload
    },
    setDisableConfirm(state, payload){
      state.disableConfirm = payload
    },
  },
  getters: {
    isAlertModalOpen(state) {
      return state.isOpen && state.modalType === 'alert'
    },
    isInputModalOpen(state) {
      return state.isOpen && state.modalType === 'input'
    },
    getContent(state) {
      return state.content
    },
    getInputValue(state) {
      return state.inputValue
    },
    getPlaceholder(state) {
      return state.placeholder
    },
    handleModalClose(state) {
      return () => {
        state.isOpen = false
        state.onClose?.()
      }
    },
    handleModalConfirm(state) {
      return () => {
        state.isOpen = false
        if (state.modalType === 'input') {
          state.onConfirm?.(state.inputValue)
        } else if (state.modalType === 'alert') {
          (state.onConfirm as (() => void) | null)?.()
        }
      }
    },
    getSelectBoxValue(state) {
      return state.selectBoxValue
    },
    isVacation(state) {
      return state.isVacation
    },
    getConfirmText(state) {
      return state.confirmText
    },
    getCloseText(state) {
      return state.closeText
    },
    getDisableConfirm(state) {
      return state.disableConfirm
    }
  }
}

export default modalModule
