package com.github.eirikb.avajavascripttestrunnerrunconfigurationgenerator.services

import com.github.eirikb.avajavascripttestrunnerrunconfigurationgenerator.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
