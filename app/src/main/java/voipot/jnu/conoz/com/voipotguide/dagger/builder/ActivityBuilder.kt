package voipot.jnu.conoz.com.voipotguide.dagger.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import voipot.jnu.conoz.com.voipotguide.ui.login.LoginFragmentProvider
import voipot.jnu.conoz.com.voipotguide.ui.main.MainActivity
import voipot.jnu.conoz.com.voipotguide.ui.main.MainModule
import voipot.jnu.conoz.com.voipotguide.ui.schedule.ScheduleFragmentProvider

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainModule::class,
        LoginFragmentProvider::class,
        ScheduleFragmentProvider::class])
    abstract fun bindMainMvvmActivity(): MainActivity
}