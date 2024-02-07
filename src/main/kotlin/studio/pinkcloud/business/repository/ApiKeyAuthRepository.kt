package studio.pinkcloud.business.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.lib.model.Application
import studio.pinkcloud.module.apikeyauth.business.repository.IApiKeyAuthRepository

object ApiKeyAuthRepository : IApiKeyAuthRepository {
  override suspend fun validateApplication(apiKey: String): Boolean {
    val filter = Filters.eq(Application::apiKey.name, apiKey)

    AppDbContext.applications.find<Application>(filter)
      .firstOrNull()
      ?.also {
        val params = Updates.currentDate(Application::lastSeenAt.name)
        AppDbContext.applications.updateOne(filter, params)

        return true
      }

    return false
  }
}
