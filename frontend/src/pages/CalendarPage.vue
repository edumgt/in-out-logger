<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { computed, h, nextTick, onMounted, onUnmounted, ref } from 'vue'
import axios from '@/utils/axios.ts'
import { sleep, useProgress } from '@/utils/etc.ts'
import { useStore } from 'vuex'
import { InputValue, SelectOption, selectOptions } from '@/stores/vuex/modules/modal.ts'
import { CalendarOptions, DateSelectArg, EventApi, EventClickArg } from '@fullcalendar/core'
import { dateToNumber } from '@/utils/string.ts'

type Direction = 'asc' | 'desc'

interface FetchedYears {
  [key: number]: boolean;
}

const calendarRef = ref<InstanceType<typeof FullCalendar>>()
const calendarApi = computed(() => (calendarRef.value as InstanceType<typeof FullCalendar>).getApi())
const currentEvents = ref<EventApi[]>([])
const fetchedYears = ref<FetchedYears>({})

const handleWeekendsToggle = () => {
  calendarOptions.value.weekends = !calendarOptions.value.weekends // update a property
}
const selectBoxValue = computed(() => store.getters.getSelectBoxValue)

const handleDateSelect = (selectInfo: DateSelectArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'input',
    content: h('div', { 'class': 'flex justify-between' }, [
      h('p', '일정명을 입력해주세요.'),
      h('div', { 'class': `flex gap-1` }, [
        h('p', '배경색 지정'),
        h('select', {
            onChange(event: any) {
              store.commit('setSelectBoxValue', event.target.value)
            }
          },
          selectOptions.map((option: SelectOption) =>
            h('option', { value: option }, option)
          )
        )
      ])
    ]),
    placeholder: '일정명',
    options: {
      propagation: true
    },
    onConfirm: async (inputValue: InputValue) => {
      let calendarApi = selectInfo.view.calendar
      calendarApi.unselect() // clear date selection

      if (!inputValue) {
        return
      }
      const response = await axios.post('/api/calendar/events', {
        title: inputValue,
        start: selectInfo.startStr,
        end: selectInfo.endStr,
        backgroundColor: selectBoxValue.value
      })
      const { id, start, end, title } = response.data
      calendarApi.addEvent({
        id,
        title,
        start,
        end,
        allDay: true,
        backgroundColor: selectBoxValue.value
      })
    }
  })

}

const handleEventClick = (clickInfo: EventClickArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'alert',
    content: h('p', '일정을 삭제할까요?'),
    onConfirm: async () => {
      const response = await axios.delete(`/api/calendar/events/${clickInfo.event.id}`)
      console.log(response)
      clickInfo.event.remove()
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


const handleEvents = async (events: EventApi[]) => {
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

  // titleFormat(arg) {
  //   console.log('arg',arg)
  //   return arg.
  // },
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
  async datesSet(arg) {
    store.commit('setIsLoading', true)
    const { start, end } = arg
    const startTime = start.getTime()
    const endTime = end.getTime()

    // 평균 타임스탬프 계산
    const averageTime = (startTime + endTime) / 2
    const currentDate = new Date(averageTime)
    const currentYear = currentDate.getFullYear()
    if (currentYear < 2000) {
      store.commit('setModal', {
        isOpen: true,
        modalType: 'alert',
        content: h('p', '2000년 이전 날짜는 조회 불가능합니다.'),
        onClose: () => calendarApi.value.gotoDate(new Date()),
        onConfirm: () => calendarApi.value.gotoDate(new Date())
      })
      store.commit('setIsLoading', false)
      return
    }
    const events = await fetchEvents(currentYear)
    if(events){
      for (const { id, title, start, end, backgroundColor } of events) {
        calendarApi.value.addEvent({
          id,
          title,
          start,
          end,
          allDay: true,
          backgroundColor
        })
      }
    }
    store.commit('setIsLoading', false)
  }
  /* you can update a remote database when these fire:
  eventAdd:
  eventChange:
  eventRemove:
  */
})
const fetchEvents = async (year: number) => {
  if(fetchedYears.value[year]){
    return
  }
  console.log('data fetching...')
  const response = await axios.get(`/api/calendar/events/${year}`)
  fetchedYears.value[year] = true
  console.log('res', response.data)
  return response.data
}


onMounted(async () => {
  await nextTick()
  for (let i = 2000; i < 2100; i++) {
    fetchedYears.value[i] = false
  }
})

onUnmounted(() => {
  store.commit('setModal', {
    isOpen: false
  })
})

const handleCheckIn = async () => {
  try {
    await axios.post('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', e.response.data),
      modalType: 'alert'
    })
    throw e
  }
}
const handleCheckOut = async () => {
  try {
    await axios.patch('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', e.response.data)
    })
    throw e
  }
}
const checkInProgress = useProgress(handleCheckIn)
const checkOutProgress = useProgress(handleCheckOut)

const store = useStore()

const handleNavigateCalendar = (eventStartDate: string) => {
  calendarApi.value.gotoDate(eventStartDate)
}

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
              <svg class="flex-shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 16">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 8h11m0 0L8 4m4 4-4 4m4-11h3a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-3" />
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">출근</span>
            </div>
          </li>
          <li>
            <div @click="checkOutProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg class="flex-shrink-0 w-5 h-5 text-gray-500 transition duration-75 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                <path d="M5 5V.13a2.96 2.96 0 0 0-1.293.749L.879 3.707A2.96 2.96 0 0 0 .13 5H5Z" />
                <path d="M6.737 11.061a2.961 2.961 0 0 1 .81-1.515l6.117-6.116A4.839 4.839 0 0 1 16 2.141V2a1.97 1.97 0 0 0-1.933-2H7v5a2 2 0 0 1-2 2H0v11a1.969 1.969 0 0 0 1.933 2h12.134A1.97 1.97 0 0 0 16 18v-3.093l-1.546 1.546c-.413.413-.94.695-1.513.81l-3.4.679a2.947 2.947 0 0 1-1.85-.227 2.96 2.96 0 0 1-1.635-3.257l.681-3.397Z" />
                <path d="M8.961 16a.93.93 0 0 0 .189-.019l3.4-.679a.961.961 0 0 0 .49-.263l6.118-6.117a2.884 2.884 0 0 0-4.079-4.078l-6.117 6.117a.96.96 0 0 0-.263.491l-.679 3.4A.961.961 0 0 0 8.961 16Zm7.477-9.8a.958.958 0 0 1 .68-.281.961.961 0 0 1 .682 1.644l-.315.315-1.36-1.36.313-.318Zm-5.911 5.911 4.236-4.236 1.359 1.359-4.236 4.237-1.7.339.341-1.699Z" />
              </svg>
              <span class="flex-1 ms-3 whitespace-nowrap">퇴근</span>
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
