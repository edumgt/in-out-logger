<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { reactive, ref, watchEffect } from 'vue'
import { toggle } from '../components/Modal.vue'
import axios from '@/utils/axios.ts'
import { useProgress } from '@/utils/proxy.ts'

let eventGuid = 0
let todayStr = new Date().toISOString().replace(/T.*$/, '') // YYYY-MM-DD of today
let endStr = new Date(2024, 10,17).toISOString().replace(/T.*$/, '')
const createEventId = () => {
  return String(eventGuid++)
}
const INITIAL_EVENTS = [
  {
    id: createEventId(),
    title: 'All-day event',
    start: todayStr,
    // end: endStr
  },
  {
    id: createEventId(),
    title: 'Timed event',
    start: todayStr + 'T12:00:00'
  }
]
const currentEvents = ref<any[]>([])
watchEffect(() => {
  console.log(currentEvents.value)
})
const handleWeekendsToggle = () => {
  calendarOptions.value.weekends = !calendarOptions.value.weekends // update a property
}
const handleDateSelect = (selectInfo: any) => {
  let title = prompt('Please enter a new title for your event')
  let calendarApi = selectInfo.view.calendar

  calendarApi.unselect() // clear date selection

  if (title) {
    calendarApi.addEvent({
      id: createEventId(),
      title,
      start: selectInfo.startStr,
      end: selectInfo.endStr,
      allDay: selectInfo.allDay
    })
  }
}

const handleEventClick = (clickInfo: any) => {
  if (confirm(`Are you sure you want to delete the event '${clickInfo.event.title}'`)) {
    clickInfo.event.remove()
  }
}
const handleEvents = (events: any[]) => {
  currentEvents.value = events
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
  initialEvents: INITIAL_EVENTS,
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

const handleCheckIn = async () => {
  try {
    await axios.post('api/commutes')
  } catch (e){
    console.error(e)
    throw e
  }
}
const checkInProgress = useProgress(handleCheckIn)
</script>

<template>
  <div class='main-wrap'>
    <div class='sidebar'>
      <div class='sidebar-section flex flex-col gap-2'>
        <button @click="checkInProgress">출근</button>
        <button @click="">퇴근</button>
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
      <FullCalendar class='calendar-app' :options='calendarOptions'>
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
