package kr.co.yeeunlee.own.project1.mywriting.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kr.co.yeeunlee.own.project1.mywriting.data.firebase.FirebaseSettings
import kr.co.yeeunlee.own.project1.mywriting.utils.GoogleSignInHelper
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
class ActivityModule {

    @Provides
    @ActivityScoped
    fun provideGoogleSignInHelper(applicationContext: Context, firebaseSettings: FirebaseSettings): GoogleSignInHelper {
        return GoogleSignInHelper(applicationContext, firebaseSettings)
    }

}