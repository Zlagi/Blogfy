package com.zlagi.domain.taskmanager

class BlogTask private constructor(val blogPk: String, val action: BlogTaskAction) {
    companion object {
        fun create(blogPk: String) = BlogTask(blogPk, BlogTaskAction.CREATE)
        fun update(blogPk: String) = BlogTask(blogPk, BlogTaskAction.UPDATE)
        fun delete(blogPk: String) = BlogTask(blogPk, BlogTaskAction.DELETE)
    }
}

enum class BlogTaskAction {
    CREATE, UPDATE, DELETE
}