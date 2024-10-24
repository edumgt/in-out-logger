import { h, VNode } from 'vue'
import { useStore } from 'vuex'
import { ModalModuleState } from '../stores/vuex/modules/modal.ts'

export type TableHeaders = Record<string, string | TableHeaderAttributes>

export interface TableHeaderAttributes {
  label: string
  hidden: boolean
}


export type TableData<T extends TableHeaders> = {
  [K in keyof T]: string
}

export type TableCellCallback<T extends TableHeaders> = {
  [K in keyof T]?: (rowData: TableData<T>, cell: TableCellAttributes<T>) => void
}

export interface TableCellAttributes<T extends TableHeaders> {
  value: string | number
  name: keyof T
}

export type TableRowCallback<T extends TableHeaders> = (rowData: TableData<T>) => void

export interface TableOptions<T extends TableHeaders> {
  onCellClick?: TableCellCallback<T>
  onCellRightClick?: TableCellCallback<T>
  onRowClick?: TableRowCallback<T>
  onRowRightClick?: TableRowCallback<T>
  additionalPayload?: Partial<ModalModuleState>
}

const useTable = () => {
  const store = useStore()
  return <T extends TableHeaders>(tableHeaders: T, data: TableData<T>[], options?: TableOptions<T>) => {
    const { onCellClick, onRowClick, onCellRightClick, onRowRightClick, additionalPayload } = options ?? {}
    store.commit('setModal', {
      isOpen: true,
      ...additionalPayload,
      content: h('table', { class: 'min-w-full border-collapse border border-gray-200' }, [
        // 테이블 헤더
        h('thead', {}, [
          h('tr', { class: 'bg-gray-200' }, Object.values(tableHeaders).reduce((acc, header) => {
              if (typeof header === 'string') {
                acc.push(h('th', { class: 'px-4 py-2 border border-gray-300 text-left text-gray-600 font-medium' }, header))
              } else {
                if (header.hidden) {
                  return acc // hidden만 주려다가 그냥 패스
                }
                acc.push(h('th', { class: 'px-4 py-2 border border-gray-300 text-left text-gray-600 font-medium' }, header.label))
              }
              return acc
            }, [] as VNode[])
          )
        ]),
        // 테이블 바디
        h('tbody', {}, data.map((row: TableData<T>, index: number) =>
          h('tr', {
            class: (index % 2 === 0 ? 'bg-white' : 'bg-gray-50') + (onRowClick && ' hover:font-bold cursor-pointer'),
            onClick: () => onRowClick?.(row),
            onContextmenu: (e) => {
              e.preventDefault()
              onRowRightClick?.(row)
            }
          }, Object.keys(tableHeaders).reduce((acc, key) => {
              if (typeof tableHeaders[key] === 'string') {
                acc.push(h('td', {
                  class: 'px-4 py-2 border border-gray-300 text-gray-700' + (onCellClick?.[key] && ' hover:font-bold cursor-pointer'),
                  onClick: (e) => {
                    if (onCellClick?.[key]) { // cell click 이벤트있다면 row click 이벤트 실행시키지 않음
                      e.stopPropagation()
                    }
                    onCellClick?.[key]?.(row, { value: row[key], name: key })
                  },
                  onContextmenu: (e) => {
                    e.preventDefault()
                    onCellRightClick?.[key]?.(row, { value: row[key], name: key })
                  }
                }, row[key]))
              }
              return acc
            }
            , [] as VNode[]))
        ))
      ])
    })
  }
}
export default useTable
