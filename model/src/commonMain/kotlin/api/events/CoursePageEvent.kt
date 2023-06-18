package org.solvo.model.api.events

interface CoursePageEvent : Event {
    val courseCode: String
}
