<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { computed, h, nextTick, onMounted, ref } from 'vue'
import axios from '@/utils/axios.ts'
import { useProgress } from '@/utils/proxy.ts'
import { useStore } from 'vuex'
import { InputValue } from '@/stores/vuex/modules/modal.ts'
import { DateSelectArg, EventApi, EventClickArg } from '@fullcalendar/core'


const calendarRef = ref<InstanceType<typeof FullCalendar>>()
const calendarApi = computed(() => (calendarRef.value as InstanceType<typeof FullCalendar>).getApi())
const currentEvents = ref<EventApi[]>([])


const handleWeekendsToggle = () => {
  calendarOptions.value.weekends = !calendarOptions.value.weekends // update a property
}


const handleDateSelect = (selectInfo: DateSelectArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'input',
    content: h('p', '일정명을 입력해주세요.'),
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
        end: selectInfo.endStr
      })
      const { id, start, end, title } = response.data
      calendarApi.addEvent({
        id,
        title,
        start,
        end,
        allDay: true
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
const handleEvents = (events: EventApi[]) => {
  currentEvents.value = events
  console.log('set', currentEvents.value)
}

const calendarOptions = ref<object>({
  plugins: [
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin // dateClick
  ],
  headerToolbar: {
    left: 'prev',
    center: 'title',
    right: 'next'
  },
  initialView: 'dayGridMonth',
  initialDate: new Date(),
  editable: true,
  selectable: true,
  selectMirror: true,
  dayMaxEvents: true,
  weekends: true,
  select: handleDateSelect,
  eventClick: handleEventClick,
  eventsSet: handleEvents
  /* you can update a remote database when these fire:
  eventAdd:
  eventChange:
  eventRemove:
  */
})
const fetchInitialEvents = async () => {
  const response = await axios.get('/api/calendar/events')
  return response.data
}

onMounted(async () => {
  await nextTick()
  const events = await fetchInitialEvents()
  for (const { id, title, start, end } of events) {
    calendarApi.value.addEvent({
      id,
      title,
      start,
      end,
      allDay: true
    })
  }
})

const handleCheckIn = async () => {
  try {
    await axios.post('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', e.response.data)
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

</script>

<template>
  <div class='main-wrap'>
    <div class='sidebar'>
      <div class='sidebar-section flex flex-col gap-2'>
        <button @click="checkInProgress">출근</button>
        <button @click="checkOutProgress">퇴근</button>
      </div>
      <div class='sidebar-section'>
        <label> <input type='checkbox' :checked='calendarOptions.weekends' @change='handleWeekendsToggle' /> toggle
          weekends </label>
      </div>
      <div class='sidebar-section'>
        <h2>All Events ({{ currentEvents.length }})</h2>
        <ul>
          <li v-for='event in currentEvents' :key='event.id'>
            <b>{{ event.startStr }}</b> <i>{{ event.title }}</i>
          </li>
        </ul>
      </div>
    </div>
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
