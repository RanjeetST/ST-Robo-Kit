package com.st.robotics.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import com.st.robotics.BuildConfig
import com.st.robotics.api.DatasetApi
import com.st.robotics.api.ProjectApi
import com.st.robotics.api.ProjectExampleApi
import com.st.robotics.api.SecretsApi
import com.st.robotics.api.TrackingApi
import com.st.robotics.utilities.LoginSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object  VespucciNetModule {

    private const val COOKIE_HEADER = "Cookie"
    private const val AUTH_HEADER = "Authorization"
    private const val BEARER_PREFIX = "Bearer "
    private const val CONNECT_TIMEOUT = 20L
    private const val READ_TIMEOUT = 20L
    private const val WRITE_TIMEOUT = 20L

    @Provides
    fun provideProjectApi(@RetrofitClient retrofitClient: Retrofit): ProjectApi =
        retrofitClient.create(ProjectApi::class.java)

    @Provides
    fun provideProjectExampleApi(@RetrofitPrjExampleClient provideRetrofitPrjExampleClient: Retrofit): ProjectExampleApi =
        provideRetrofitPrjExampleClient.create(ProjectExampleApi::class.java)

    @Provides
    fun provideDatasetApi(@RetrofitDatasetClient retrofitClient: Retrofit): DatasetApi =
        retrofitClient.create(DatasetApi::class.java)

    @Provides
    fun provideTrackingApi(@RetrofitTrackingClient retrofitClient: Retrofit): TrackingApi =
        retrofitClient.create(TrackingApi::class.java)

    @Provides
    fun provideSecretsApi(@RetrofitSecretsClient retrofitClient: Retrofit): SecretsApi =
        retrofitClient.create(SecretsApi::class.java)

    @Provides
    @VespucciLoggingLevel
    fun provideHttpLoggingInterceptorLevel(): HttpLoggingInterceptor.Level =
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @VespucciLoggingInterceptor
    fun provideLoggingInterceptor(
        @VespucciLoggingLevel loggingLevel: HttpLoggingInterceptor.Level
    ): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            setLevel(level = loggingLevel)
            if (BuildConfig.DEBUG.not()) {
                redactHeader(name = AUTH_HEADER)
                redactHeader(name = COOKIE_HEADER)
            }
        }

    @Provides
    @AuthInterceptor
    fun provideAuthInterceptor(
        vespucciSession: LoginSession
    ): Interceptor =
        Interceptor { chain ->
            val response = runBlocking {
                try {
                    val token = vespucciSession.getLastVespucciSession().qrCodeToken
                        ?: null ?: ""

                    val request = chain.request().newBuilder().addHeader(
                        name = AUTH_HEADER,
                        value = (BEARER_PREFIX + token)
                    ).build()
                    chain.proceed(request = request)
                } catch (ex: Exception) {
                    Log.w("VespucciNetModule", ex.localizedMessage ?: "")

                    chain.proceed(request = chain.request())
                }
            }
            return@Interceptor response
        }

    @Provides
    @AuthNoInterceptor
    fun provideNoAuthInterceptor(): Interceptor =
        Interceptor { chain ->
            val response = runBlocking {
                try {
                    val request = chain.request().newBuilder().build()
                    chain.proceed(request = request)
                } catch (ex: Exception) {
                    Log.w("VespucciNetModule", ex.localizedMessage ?: "")

                    chain.proceed(request = chain.request())
                }
            }
            return@Interceptor response
        }

    @Provides
    @AuthInterceptorDataset
    fun provideAuthInterceptorDataset(
        vespucciSession: LoginSession
    ): Interceptor =
        Interceptor { chain ->
            val response = runBlocking {
                try {
                    val token = vespucciSession.getLastVespucciSession().qrCodeIdToken
                        ?: null ?: ""

                    val request = chain.request().newBuilder().addHeader(
                        name = AUTH_HEADER,
                        value = (BEARER_PREFIX + token)
                    ).build()
                    chain.proceed(request = request)
                } catch (ex: Exception) {
                    Log.w("VespucciNetModule", ex.localizedMessage ?: "")

                    chain.proceed(request = chain.request())
                }
            }
            return@Interceptor response
        }

    @Provides
    @AuthInterceptorTracking
    fun provideAuthInterceptorTracking(
        vespucciSession: LoginSession
    ): Interceptor =
        Interceptor { chain ->
            val response = runBlocking {
                try {
                    val token = vespucciSession.getLastVespucciSession().qrCodeIdToken
                        ?: null ?: ""

                    val request = chain.request().newBuilder().addHeader(
                        name = AUTH_HEADER,
                        value = (BEARER_PREFIX + token)
                    ).build()
                    chain.proceed(request = request)
                } catch (ex: Exception) {
                    Log.w("VespucciNetModule", ex.localizedMessage ?: "")

                    chain.proceed(request = chain.request())
                }
            }
            return@Interceptor response
        }

    @Provides
    @RetrofitBasePath
    fun provideBasePath(
    ): String = BuildConfig.BASE_URL

    @Provides
    @RetrofitPrjExampleBasePath
    fun providePrjExampleBasePath(
    ): String = BuildConfig.BASE_URL

    @Provides
    @RetrofitDatasetBasePath
    fun provideDatasetBasePath(
    ): String = BuildConfig.DATASET_BASE_URL

    @Provides
    @RetrofitTrackingBasePath
    fun provideTrackingBasePath(
    ): String = BuildConfig.TRACKING_BASE_URL

    @Provides
    @RetrofitSecretsBasePath
    fun provideSecretsBasePath(
    ): String = BuildConfig.SECRETS_BASE_URL

    @Provides
    @HttpClient
    fun provideHttpClient(
        @VespucciLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptor authInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        callTimeout(timeout = CONNECT_TIMEOUT, unit = TimeUnit.SECONDS)
        readTimeout(timeout = READ_TIMEOUT, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = WRITE_TIMEOUT, unit = TimeUnit.SECONDS)
        addInterceptor(interceptor = authInterceptor)
        addInterceptor(interceptor = loggingInterceptor)
    }.build()

    @Provides
    @HttpNoAuthClient
    fun provideNoAuthHttpClient(
        @AuthNoInterceptor authNoInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        callTimeout(timeout = CONNECT_TIMEOUT, unit = TimeUnit.SECONDS)
        readTimeout(timeout = READ_TIMEOUT, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = WRITE_TIMEOUT, unit = TimeUnit.SECONDS)
        addInterceptor(interceptor = authNoInterceptor)
    }.build()

    @Provides
    @HttpClientDataset
    fun provideHttpClientDataset(
        @VespucciLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptorDataset authInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        callTimeout(timeout = CONNECT_TIMEOUT, unit = TimeUnit.SECONDS)
        readTimeout(timeout = READ_TIMEOUT, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = WRITE_TIMEOUT, unit = TimeUnit.SECONDS)
        addInterceptor(interceptor = authInterceptor)
        addInterceptor(interceptor = loggingInterceptor)
    }.build()

    @Provides
    @HttpClientTracking
    fun provideHttpClientTracking(
        @VespucciLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptorTracking authInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        callTimeout(timeout = CONNECT_TIMEOUT, unit = TimeUnit.SECONDS)
        readTimeout(timeout = READ_TIMEOUT, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = WRITE_TIMEOUT, unit = TimeUnit.SECONDS)
        addInterceptor(interceptor = authInterceptor)
        addInterceptor(interceptor = loggingInterceptor)
    }.build()

    @Provides
    @UnAuthHttpClient
    fun provideUnAuthHttpClient(
        @VespucciLoggingInterceptor loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        callTimeout(timeout = CONNECT_TIMEOUT, unit = TimeUnit.SECONDS)
        readTimeout(timeout = READ_TIMEOUT, unit = TimeUnit.SECONDS)
        writeTimeout(timeout = WRITE_TIMEOUT, unit = TimeUnit.SECONDS)
        addInterceptor(interceptor = loggingInterceptor)
    }.build()

    @Provides
    @RetrofitClient
    fun provideRetrofitClient(
        @HttpClient httpClient: OkHttpClient,
        @RetrofitBasePath basePath: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(basePath)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    @RetrofitPrjExampleClient
    fun provideRetrofitPrjExampleClient(
        @HttpNoAuthClient httpNoAuthClient: OkHttpClient,
        @RetrofitPrjExampleBasePath prjExampleBasePath: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpNoAuthClient)
        .baseUrl(prjExampleBasePath)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    @RetrofitDatasetClient
    fun provideRetrofitDatasetClient(
        @HttpClientDataset httpClient: OkHttpClient,
        @RetrofitDatasetBasePath basePath: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(basePath)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    @RetrofitTrackingClient
    fun provideRetrofitTrackingApiClient(
        @HttpClientTracking httpClient: OkHttpClient,
        @RetrofitTrackingBasePath basePath: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(basePath)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    @RetrofitSecretsClient
    fun provideRetrofitSecretsClient(
        @UnAuthHttpClient httpClient: OkHttpClient,
        @RetrofitSecretsBasePath basePath: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(basePath)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @HttpClient httpClient: OkHttpClient
    ): ImageLoader =
        ImageLoader.Builder(context)
            .okHttpClient(httpClient)
            .build()
}
