package studio.pinkcloud.module.apikeyauth.business.repository

interface IApiKeyAuthRepository {
  suspend fun validateApplication(apiKey: String): Boolean
}
