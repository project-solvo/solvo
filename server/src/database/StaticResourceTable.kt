package org.solvo.server.database

import org.jetbrains.exposed.dao.id.UUIDTable

object StaticResourceTable: UUIDTable("StaticResources", "ResourceId")