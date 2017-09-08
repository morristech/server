package tech.pronghorn.server.services

import tech.pronghorn.coroutines.service.InternalQueueService
import tech.pronghorn.server.HttpServerConnection
import tech.pronghorn.server.HttpServerWorker

class ConnectionReadService(override val worker: HttpServerWorker) : InternalQueueService<HttpServerConnection>() {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun process(connection: HttpServerConnection): Boolean {
        connection.isReadQueued = false
        connection.readRequests()
        return true
    }
}
