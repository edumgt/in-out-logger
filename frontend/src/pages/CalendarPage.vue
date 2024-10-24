<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { computed, h, onMounted, onUnmounted, ref } from 'vue'
import axios from '@/utils/axios.ts'
import useProgress from '@/hooks/useProgress.ts'
import { useStore } from 'vuex'
import { InputHandler, InputValue, SelectOption, selectOptions } from '@/stores/vuex/modules/modal.ts'
import { CalendarOptions, DateSelectArg, EventApi, EventClickArg } from '@fullcalendar/core'
import { dateToNumber } from '@/utils/string.ts'
import { AxiosError, AxiosResponse, isAxiosError } from 'axios'
import { messageHandler } from '@/utils/error.ts'
import { vacation, VacationType } from '@/types/vacation.ts'
import useTable, { TableHeaders } from '@/hooks/useTable.ts'
import regex from '@/utils/regex.ts'
import { isAdmin, jobLevels } from '@/utils/auth.ts'

type Direction = 'asc' | 'desc'

interface FetchedYears {
  [key: number]: boolean;
}

const vacationType = ref<VacationType>('종일 휴가')
const calendarRef = ref<InstanceType<typeof FullCalendar>>()
const calendarApi = computed(() => (calendarRef.value as InstanceType<typeof FullCalendar>).getApi())
const currentEvents = ref<EventApi[]>([])
const fetchedYears = ref<FetchedYears>({})

const handleWeekendsToggle = () => {
  calendarOptions.value.weekends = !calendarOptions.value.weekends // update a property
}

const isVacation = computed(() => store.getters.isVacation)
const selectBoxValue = computed(() => store.getters.getSelectBoxValue)
const handleDateSelect = (selectInfo: DateSelectArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'input',
    content: h('div', { 'class': 'flex justify-between' }, [
      h('div', { 'class': 'flex flex-col' }, [
        h('p', '일정명을 입력해주세요.'),
        h('div', { 'class': 'flex flex-row pt-2 pl-1 gap-2' }, [
          h('label', { 'class': 'text-xs', 'for': 'isVacation' }, '휴가입니다.'),
          h('input', {
              'type': 'checkbox',
              onChange(event: any) {
                store.commit('setIsVacation', event.target.checked)
                if (event.target.checked) {
                  store.commit('setPlaceholder', '연차 사유')
                  event.target.nextSibling.classList.remove('invisible')
                } else {
                  store.commit('setPlaceholder', '일정명')
                  event.target.nextSibling.classList.add('invisible')
                }
              }
            }
          ),
          h('select', {
              'class': 'invisible py-1', 'id': 'vacationType',
              onChange(event: any) {
                vacationType.value = event.target.value
              }
            }, Object.values(vacation).map(name => h('option', { 'value': name }, name))
          )
        ])
      ]),
      h('div', { 'class': `flex gap-1` }, [
        h('p', { 'class': 'mt-2 mr-1' }, '배경색 지정'),
        h('select', {
            onChange(event: any) {
              store.commit('setSelectBoxValue', event.target.value)
            },
            'class': 'absolute'
          },
          selectOptions.map((option: SelectOption) =>
            h('option', { value: option }, option)
          )
        )
      ])
    ]),
    placeholder: '일정명',
    onConfirm: async (inputValue: InputValue) => {
      let calendarApi = selectInfo.view.calendar
      calendarApi.unselect() // clear date selection

      if (!inputValue) {
        return
      }
      try {
        const data = await axios.post('/api/calendar/events', {
          title: inputValue,
          start: selectInfo.startStr,
          end: selectInfo.endStr,
          backgroundColor: selectBoxValue.value,
          isVacation: isVacation.value,
          vacationType: vacationType.value
        }).then((res: AxiosResponse) => res.data)
        if (isVacation.value) {
          store.commit('setModal', {
            isOpen: true,
            content: h('p', '휴가 신청이 완료되었습니다.')
          })
        }
        const { id, start, end, title, backgroundColor, vacationStatus } = data
        calendarApi.addEvent({
          id,
          title: `${title} ${vacationStatus ?? ''}`,
          start,
          end,
          allDay: true,
          backgroundColor
        })
      } catch (e) {
        if (isAxiosError(e)) {
          store.commit('setModal', {
            isOpen: true,
            content: h('p', messageHandler(e))
          })
        }
        throw e
      } finally {
        vacationType.value = '종일 휴가' // 초기화
      }
    }
  })

}

const handleEventClick = (clickInfo: EventClickArg) => {
  store.commit('setModal', {
    isOpen: true,
    content: h('p', '일정을 삭제할까요?'),
    onConfirm: async () => {
      try {
        await axios.delete(`/api/calendar/events/${clickInfo.event.id}`)
        clickInfo.event.remove()
      } catch (e: any) {
        if (isAxiosError(e)) {
          store.commit('setModal', {
            isOpen: true,
            content: h('p', messageHandler(e))
          })
        }
      }
    }
  })
}

const direction = ref<Direction>('asc')

const handleClickCheckbox = () => {
  const opposite: Direction = direction.value === 'asc' ? 'desc' : 'asc'
  direction.value = opposite
  sortEvents()
}

const sortEvents = () => {
  if (direction.value === 'asc') {
    currentEvents.value.sort((a, b) => dateToNumber(a.startStr) - dateToNumber(b.startStr))
  } else {
    currentEvents.value.sort((a, b) => dateToNumber(b.startStr) - dateToNumber(a.startStr))
  }
}


const handleEvents = (events: EventApi[]) => {
  currentEvents.value = events
  sortEvents()
  console.log('set', events[0]?.startStr)
}
const calendarOptions = ref<CalendarOptions>({
  plugins: [
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin // dateClick
  ],
  headerToolbar: {
    left: 'prevYear prev',
    center: 'title',
    right: 'next nextYear'
  },
  locale: 'kr',
  initialView: 'dayGridMonth',
  initialDate: new Date(),
  editable: true,
  selectable: true,
  selectMirror: true,
  dayMaxEvents: true,
  weekends: true,
  select: handleDateSelect,
  eventClick: handleEventClick,
  eventsSet: handleEvents,
  // async eventDragStop(arg) {
  //   console.log('drag end', arg.event.startStr,arg.event.endStr)
  // },
  eventDrop(arg) {
    axios.patch('/api/calendar/events/' + arg.event.id, {
      start: arg.event.startStr,
      end: arg.event.endStr
    }).catch((e: AxiosError) => {
      store.commit('setModal', {
        isOpen: true,
        content: h('p', messageHandler(e)),
        onClose: () => arg.revert(),
        onConfirm: () => arg.revert()
      })
      throw e
    })


  },
  async datesSet(arg) {
    store.commit('setIsLoading', true)
    const { start, end } = arg
    const startTime = start.getTime()
    const endTime = end.getTime()

    // 평균 타임스탬프 계산
    const averageTime = (startTime + endTime) / 2
    const currentDate = new Date(averageTime)
    const currentYear = currentDate.getFullYear()
    const ALLOW_YEAR = 2000
    if (currentYear < ALLOW_YEAR) {
      const onClose = () => calendarApi.value.gotoDate(new Date(ALLOW_YEAR, 0, 1))
      store.commit('setModal', {
        isOpen: true,
        content: h('p', { 'class': 'font-bold' }, `${ALLOW_YEAR}년 이전 날짜는 조회 불가능합니다.`),
        onClose,
        onConfirm: onClose
      })
      store.commit('setIsLoading', false)
      return
    }
    try {
      const events = await fetchEvents(currentYear)
      if (events) {
        for (const { id, title, start, end, backgroundColor, vacationStatus } of events) {
          calendarApi.value.addEvent({
            id,
            title: `${title} ${vacationStatus ?? ''}`,
            start,
            end,
            allDay: true,
            backgroundColor
          })
        }
      }
      const holidays = await axios.get(`/api/calendar/events/holiday/years/${currentYear}`)
      console.log(holidays.response.data)
    } finally {
      store.commit('setIsLoading', false)
    }
  }
})
const fetchEvents = async (year: number) => {
  if (fetchedYears.value[year]) {
    return
  }
  console.log('data fetching...')
  const response = await axios.get(`/api/calendar/events/${year}`)
  fetchedYears.value[year] = true
  return response.data
}


onMounted(async () => {
  for (let i = 2000; i < 2100; i++) {
    fetchedYears.value[i] = false
  }
})

onUnmounted(() => {
  store.getters.handleModalClose()
})

const handleCheckIn = async () => {
  try {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', '출근처리 할까요?'),
      confirmText: '출근',
      onConfirm: async () => {
        const message = await axios.post('/api/commute').then((res: AxiosResponse) => res.data).catch((e: AxiosError) => e.response?.data)
        store.commit('setModal', {
            isOpen: true,
            content: h('p', message)
        })
      }
    })
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', messageHandler(e))
    })
    throw e
  }
}
const handleCheckOut = async () => {
  try {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', '퇴근처리 할까요?'),
      confirmText: '퇴근',
      onConfirm: async () => {
        const message = await axios.patch('/api/commute').then((res: AxiosResponse) => res.data).catch((e: AxiosError) => e.response?.data)
        store.commit('setModal', {
          isOpen: true,
          content: h('p', message)
        })
      }
    })
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', messageHandler(e))
    })
    throw e
  }
}
const checkInProgress = useProgress(handleCheckIn)
const checkOutProgress = useProgress(handleCheckOut)

const annualLeaveTableHeader: TableHeaders = {
  employeeName: '이름',
  annualLeave: '잔여 연차'
}
const viewAnnualLeave = async () => {
  const handleCellClick = (message: string, inputHandler?: InputHandler) => {
    return async (rowData, cell) => {
      store.commit('setModal', {
        isOpen: true,
        modalType: 'input',
        content: h('p', message),
        placeholder: annualLeaveTableHeader[cell.name],
        inputValue: rowData[cell.name],
        onConfirm: async (input) => {
          const message = await axios.patch(`/api/employees/${rowData.id}`, {
            [cell.name]: input
          }).then((res: AxiosResponse) => res.data)
            .catch((e: AxiosError) => e.response?.data)
          store.commit('setModal', {
            isOpen: true,
            content: h('p', message),
            onClose: setEmployeeTable,
            closeText: '뒤로'
          })
        },
        onClose: setEmployeeTable,
        closeText: '뒤로',
        inputHandler
      })
    }
  }
  const setEmployeeTable = async () => {
    const detailData = await axios.get('/api/employees').then((res: AxiosResponse) => res.data)
    table(employeeDetailsHeader, detailData, {
      additionalPayload: {
        closeText: '뒤로',
        onClose: setAnnualLeaveTable
      },
      onCellClick: {
        phoneNumber: handleCellClick('핸드폰 번호를 입력해주세요.', (input, prev) => {
          input = input.replace(/[^0-9]/g, '').replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3').replace(/(\-{1,2})$/g, '')
          if (regex.phoneNumber.test(input)) {
            store.commit('setDisableConfirm', false)
            return input
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        hireDate: handleCellClick('yyyy-MM-dd 형식으로 입력해주세요.', (input, prev) => {
          if (regex.yyyyMMdd.test(input)) {
            store.commit('setDisableConfirm', false)
            return input
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        jobLevel: handleCellClick('1 ~ 8 숫자 입력 바랍니다.', (input, prev) => {
          if (input.length > 1) {
            input = input.slice(-1)
          }
          const number = Number(input)
          if (!isNaN(number)) {
            if (number >= 1 && number <= jobLevels.length) {
              store.commit('setDisableConfirm', false)
              return jobLevels[number - 1]
            }
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        address: handleCellClick('주소를 입력해주세요.'),
        annualLeave: handleCellClick('연차 갯수를 입력해주세요.', (input, prev) => {
          const number = Number(input)
          if (!isNaN(number)) {
            return number
          }
          return prev
        }),
        department: handleCellClick('부서를 입력해주세요.')
      } // end of cellClick
    })
  }


  const data = await axios.get('/api/employees/vacations').then((res: AxiosResponse) => res.data)
  const annualLeaveDetailsHeader = {
    start: '시작일',
    end: '종료일',
    reason: '사유',
    vacationType: '분류',
    vacationStatus: '상태',
    approvedAt: '승인일자',
    approvedBy: '승인자'
  }
  const setAnnualLeaveTable = () => {
    table(annualLeaveTableHeader, data, {
      onCellClick: {
        employeeName: setEmployeeTable,
        annualLeave: async (rowData, cell) => {
          const annualLeaveDetails = await axios.get(`/api/employees/${rowData.employeeId}/vacations`).then((res: AxiosResponse) => res.data)
          table(annualLeaveDetailsHeader, annualLeaveDetails, {
            additionalPayload: {
              onClose: setAnnualLeaveTable,
              closeText: '뒤로'
            }
          })
        }
      }
    })
  }
  setAnnualLeaveTable()
}
const lateEmployeeTableHeader: TableHeaders = {
  date: '날짜',
  lateEmployeeName: '이름',
  lateCount: '지각 횟수'
}
const employeeDetailsHeader: TableHeaders = {
  name: '이름',
  phoneNumber: '핸드폰 번호',
  hireDate: '입사 일자',
  jobLevel: '직급',
  address: '주소',
  annualLeave: '잔여 연차',
  department: '부서'
}
const lateDetailsHeader: TableHeaders = {
  date: '날짜',
  employeeName: '이름',
  checkInTime: '출근',
  checkOutTime: '퇴근'
}
// 출근 조회
const viewCommute = async () => {

  // 셀 클릭
  const handleCellClick = (message: string, inputHandler?: InputHandler) => {
    return async (rowData, cell) => {
      store.commit('setModal', {
        isOpen: true,
        modalType: 'input',
        content: h('p', message),
        placeholder: employeeDetailsHeader[cell.name],
        inputValue: rowData[cell.name],
        onConfirm: async (input: string) => {
          const message = await axios.patch(`/api/employees/${rowData.id}`, {
            [cell.name]: input
          }).then((res: AxiosResponse) => res.data)
            .catch((e: AxiosError) => e.response?.data)
          store.commit('setModal', { // 결과 모달 핸들링
            isOpen: true,
            content: h('p', message),
            onClose: setEmployeeTable,
            closeText: '뒤로'
          })
        },
        onClose: setEmployeeTable,
        closeText: '뒤로',
        inputHandler
      })
    }
  }
  const commuteData = await axios.get('/api/commute/employees/late').then((res: AxiosResponse) => res.data)

  // 사원 정보 테이블 렌더
  const setEmployeeTable = async () => {
    const detailData = await axios.get('/api/employees').then((res: AxiosResponse) => res.data)
    table(employeeDetailsHeader, detailData, {
      additionalPayload: {
        closeText: '뒤로',
        onClose: setCommuteTable
      },
      onCellClick: {
        phoneNumber: handleCellClick('핸드폰 번호를 입력해주세요.', (input, prev) => {
          input = input.replace(/[^0-9]/g, '').replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3').replace(/(\-{1,2})$/g, '')
          if (regex.phoneNumber.test(input)) {
            store.commit('setDisableConfirm', false)
            return input
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        hireDate: handleCellClick('yyyy-MM-dd 형식으로 입력해주세요.', (input, prev) => {
          if (regex.yyyyMMdd.test(input)) {
            store.commit('setDisableConfirm', false)
            return input
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        jobLevel: handleCellClick('1 ~ 8 숫자 입력 바랍니다.', (input, prev) => {
          if (input.length > 1) {
            input = input.slice(-1)
          }
          const number = Number(input)
          if (!isNaN(number)) {
            if (number >= 1 && number <= jobLevels.length) {
              store.commit('setDisableConfirm', false)
              return jobLevels[number - 1]
            }
          }
          store.commit('setDisableConfirm', true)
          return prev
        }),
        address: handleCellClick('주소를 입력해주세요.'),
        annualLeave: handleCellClick('연차 갯수를 입력해주세요.', (input, prev) => {
          const number = Number(input)
          if (!isNaN(number)) {
            return number
          }
          return prev
        }),
        department: handleCellClick('부서를 입력해주세요.')
      } // end of cellClick
    })
  }
  const setCommuteTable = () => {
    table(lateEmployeeTableHeader, commuteData, {
      onCellClick: {
        lateEmployeeName: setEmployeeTable,
        date: (rowData) => {
          calendarApi.value.gotoDate(rowData.date)
          store.getters.handleModalClose()
        },
        lateCount: async (rowData) => {
          const { employeeId, date } = rowData
          const [year, month, _day] = date.split('-')
          const detailData = await axios.get(`/api/commute/employees/${employeeId}/late/years/${year}/months/${month}`).then((res: AxiosResponse) => res.data)
          table(lateDetailsHeader, detailData, {
            additionalPayload: {
              closeText: '뒤로',
              onClose: setCommuteTable
            }
          }) // end of inner table
        } // end of callback
      }
    }) // end of outer table
  }
  setCommuteTable()

}

const annualLeaveApprovalHeader = {
  employeeName: '이름',
  start: '시작일',
  end: '종료일',
  reason: '사유',
  vacationType: '분류'
}

const viewVacationApproval = async () => {
  const data = await axios.get('/api/employees/vacations/pending').then((res: AxiosResponse) => res.data)
  const setApprovalTable = () => {
    table(annualLeaveApprovalHeader, data, {
      onRowRightClick: (rowData) => {
        const { employeeName, start, end, vacationType, vacationId } = rowData
        store.commit('setModal', {
          isOpen: true,
          modalType: 'input',
          content: h('p', `${employeeName}님의 ${vacationType}(${start}~${end}) 반려할까요?`),
          placeholder: '반려 사유',
          closeText: '뒤로',
          onClose: setApprovalTable,
          confirmText: '반려',
          onConfirm: async (reason: string) => {
            const message = await axios.delete(`/api/employees/vacations/${vacationId}/rejection?reason=${reason}`).then((res: AxiosResponse) => res.data)
            store.commit('setModal', {
              isOpen: true,
              content: h('p', message),
              closeText: '뒤로',
              onClose: setApprovalTable
            })
          }
        })
      },
      onRowClick: (rowData) => {
        const { employeeName, start, end, vacationType, vacationId } = rowData
        store.commit('setModal', {
          isOpen: true,
          content: h('p', `${employeeName}님의 ${vacationType}(${start}~${end}) 승인할까요?`),
          confirmText: '승인',
          onConfirm: async () => {
            const message = await axios.patch(`/api/employees/vacations/${vacationId}/approval`).then((res: AxiosResponse) => res.data)
            store.commit('setModal', {
              isOpen: true,
              content: h('p', message),
              closeText: '뒤로',
              onClose: setApprovalTable
            })
          },
          closeText: '뒤로',
          onClose: setApprovalTable
        })
      }
    })
  }
  setApprovalTable()
}


const table = useTable()
const store = useStore()

const handleNavigateCalendar = (eventStartDate: string) => {
  calendarApi.value.gotoDate(eventStartDate)
}
const viewCommuteProgress = useProgress(viewCommute)
const viewAnnualLeaveProgress = useProgress(viewAnnualLeave)
const viewVacationApprovalProgress = useProgress(viewVacationApproval)
</script>

<template>
  <div class='main-wrap'>
    <aside class="top-0 left-0 z-40 w-64 h-screen" aria-label="Sidebar">
      <div class="h-full px-3 py-4 overflow-y-auto bg-gray-50 dark:bg-gray-800">
        <ul class="space-y-2 font-medium">
          <li>
            <router-link to="/" class="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <path fill="currentColor" d="m12 5.69l5 4.5V18h-2v-6H9v6H7v-7.81zM12 3L2 12h3v8h6v-6h2v6h6v-8h3z" />
              </svg>
              <span class="ms-3">메인으로</span>
            </router-link>
          </li>
          <li>
            <div @click="checkInProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <g fill="currentColor">
                  <path d="M14 19a1 1 0 1 0 0 2h5a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2h-5a1 1 0 1 0 0 2h5v14z" />
                  <path d="M15.714 12.7a1 1 0 0 0 .286-.697v-.006a1 1 0 0 0-.293-.704l-4-4a1 1 0 1 0-1.414 1.414L12.586 11H3a1 1 0 1 0 0 2h9.586l-2.293 2.293a1 1 0 1 0 1.414 1.414l4-4z" />
                </g>
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">출근</span>
            </div>
          </li>
          <li>
            <div @click="checkOutProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <g fill="currentColor">
                  <path fill-rule="evenodd" d="M11 20a1 1 0 0 0-1-1H5V5h5a1 1 0 1 0 0-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h5a1 1 0 0 0 1-1" clip-rule="evenodd" />
                  <path d="M21.714 12.7a1 1 0 0 0 .286-.697v-.006a1 1 0 0 0-.293-.704l-4-4a1 1 0 1 0-1.414 1.414L18.586 11H9a1 1 0 1 0 0 2h9.586l-2.293 2.293a1 1 0 0 0 1.414 1.414l4-4z" />
                </g>
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">퇴근</span>
            </div>
          </li>
          <li>
            <div @click="viewCommuteProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <path fill="currentColor" d="M18 2a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zm0 2h-5v8l-2.5-2.25L8 12V4H6v16h12z" />
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">출근부</span>
            </div>
          </li>
          <li>
            <div @click="viewAnnualLeaveProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 48 48">
                <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="4">
                  <path d="m11 14.999l-6 1c1.63-7.514 6.364-9.993 11-10c2.997-.005 5.952 1.026 8 2c2.048-.974 5-2 8-2c4.611 0 9.37 2.486 11 10l-6-1c.559 2.1 1.788 5.792 0 9c-2.98-2.673-9.87-6.709-13-9c-3.13 2.291-10.02 6.327-13 9c-1.788-3.207-.559-6.9 0-9" />
                  <path d="M24 15c-.755 3.889-1.811 13.533 0 21" />
                  <path d="M12 42h24c-4.787-4.585-7-5.995-12-6s-10.108 3.382-12 6" />
                </g>
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">휴가 조회</span>
            </div>
          </li>
          <li v-if="isAdmin()">
            <div @click="viewVacationApprovalProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 2048 2048">
                <path fill="currentColor" d="M1728 1664q26 0 45 19t19 45t-19 45t-45 19H960q-26 0-45-19t-19-45t19-45t45-19zm-128 256q26 0 45 19t19 45t-19 45t-45 19h-512q-26 0-45-19t-19-45t19-45t45-19zm238-512h210v128H537q-10 64-14 128t-7 127t-3 128t-1 129H128v-193q0-81 6-160t17-159H0v-128h172q50-245 151-471t250-427q-146-5-268-61T82 289l-45-46l47-44q104-97 228-147T579 1q156 0 286 58t239 168q77 2 149 24t134 60t113 90t87 115t57 135t20 150v64h-64q-132 0-247-56t-199-159q-76 138-191 227t-269 125q-43 99-76 200t-57 206h289q22-84 69-154t112-122t146-79t167-29q87 0 167 28t145 80t113 121t69 155M1238 380q-6 65-26 132q51 88 134 146t185 74q-10-61-35-115t-63-101t-88-80t-107-56m-126 7q-102 8-192 50T759 547T642 704t-61 189q85-7 161-36t139-78t112-112t81-144q14-33 23-67t15-69M224 247q80 66 177 101t201 35q97 0 187-30t168-88q-80-66-177-101t-201-35q-97 0-187 30t-168 88m318 775q-8 2-16 2t-16 0h-32q-16 0-32-2v-47q-53 118-89 232t-59 230t-32 235t-10 248h128q0-121 8-234t27-223t49-219t74-222m442 386h720q-20-57-56-104t-83-81t-104-52t-117-19q-61 0-117 18t-103 52t-84 81t-56 105" />
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">휴가 결재</span>
            </div>
          </li>
          <li class='sidebar-section'>
            <label> <input type='checkbox' :checked='calendarOptions.weekends' @change='handleWeekendsToggle' /> toggle
              weekends </label>
          </li>
          <li class='sidebar-section w-full'>
            <h2>All Events ({{ currentEvents.length }})</h2>
            <div class="mt-1 flex gap-1">
              <input @change="handleClickCheckbox" :checked="direction === 'asc'" id="direction" type="checkbox" />
              <label for="direction">오름차순</label>
            </div>
            <ul>
              <li v-for='event in currentEvents' :key='event.id'>
                <div class="cursor-pointer" @click="() => handleNavigateCalendar(event.startStr)">
                  <b>{{ event.startStr }}</b> <i>{{ event.title }}</i>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </aside>

    <div class='calendar-wrap'>
      <FullCalendar class='calendar-app' ref="calendarRef" :options='calendarOptions'>
        <template v-slot:eventContent='arg'>
          <b>{{ arg.timeText }}</b> <i>{{ arg.event.title }}</i>
        </template>
      </FullCalendar>
    </div>

  </div>
</template>

<style lang='css'>
h2 {
  margin: 0;
  font-size: 16px;
}

ul {
  margin: 0;
  padding: 0 0 0 1.5em;
}

li {
  margin: 1.5em 0;
  padding: 0;
}

b { /* used for event dates/times */
  margin-right: 3px;
}

.sidebar {
  width: 300px;
  line-height: 1.5;
  background: #eaf9ff;
  border-right: 1px solid #d3e2e8;
}

.sidebar-section {
  padding: 2em;
}

.calendar-wrap {
  flex-grow: 1;
  padding: 3em;
}

.fc { /* the calendar root */
  max-width: 1100px;
  margin: 0 auto;
}

</style>
