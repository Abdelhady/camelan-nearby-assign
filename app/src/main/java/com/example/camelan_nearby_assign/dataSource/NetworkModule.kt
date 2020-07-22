package com.example.camelan_nearby_assign.dataSource

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * https://dev.to/paulodhiambo/kotlin-and-retrofit-network-calls-2353
 *
 * https://developer.android.com/training/dependency-injection/dagger-android#dagger-modules
 */
@Module
class NetworkModule {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.foursquare.com/v2/venues/")
        .addConverterFactory(GsonConverterFactory.create())
        .client( // To add common query parameters: https://stackoverflow.com/a/61178756/905801
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val url = chain
                        .request()
                        .url()
                        .newBuilder()
                        .addQueryParameter(
                            "client_id",
                            "JN522GQDONA2BMKJQCMIE5YINV3XJWQF5YL142QR01VHXYDH"
                        )
                        .addQueryParameter(
                            "client_secret",
                            "SU1OOEHB52HHKE3R34RTJYUA52QBIT0NPZGRMYNJ3CATP4PR"
                        )
                        .addQueryParameter("v", "20190425")
                        .build()
                    chain.proceed(chain.request().newBuilder().url(url).build())
                }
                .build()
        )
        .build()

    @Provides
    fun provideVenuesService(): FoursquareVenuesService {
        return retrofit.create(FoursquareVenuesService::class.java)
    }

}