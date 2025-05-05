package com.st.robotics.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitBasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitPrjExampleBasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitDatasetBasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitTrackingBasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitSecretsBasePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitPrjExampleClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitDatasetClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitTrackingClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitSecretsClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpNoAuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpClientDataset

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpClientTracking

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnAuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthNoInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorDataset

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorTracking

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VespucciLoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VespucciLoggingLevel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Preferences
