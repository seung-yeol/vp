package voipot.jnu.conoz.com.voipotguide.dagger.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.dagger.module.AppModule
import voipot.jnu.conoz.com.voipotguide.dagger.builder.ActivityBuilder
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,
    ActivityBuilder::class,
    AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<CommonApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}