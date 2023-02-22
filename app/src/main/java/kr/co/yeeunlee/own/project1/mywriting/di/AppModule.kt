package kr.co.yeeunlee.own.project1.mywriting.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.yeeunlee.own.project1.mywriting.data.firebase.FirebaseDaoImpl
import kr.co.yeeunlee.own.project1.mywriting.data.repository.Repository
import kr.co.yeeunlee.own.project1.mywriting.data.repository.RepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) // 해당 클래스 사용 컨테이너 범위 hilt에 알림
@Module // (다양한 객체 제공 방법을 알리는)모듈임을 hilt에 알림
class AppModule {

    @Singleton // 인스턴스 범위를 application 컨테이너로 지(계층 구조의 상위 컨테이너에서 사용할 수 있는 결합은 계층구조의 하위 수준에서도 사용 가능)
    @Provides // 인스턴스 제공 메서드임을 알림
    fun provideRepository(
        firebaseDaoImpl: FirebaseDaoImpl
    ): Repository {
        return RepositoryImpl(firebaseDaoImpl)
    }

    @Singleton
    @Provides
    fun provideFirebaseDaoImpl(): FirebaseDaoImpl {
        return FirebaseDaoImpl()
    }
}