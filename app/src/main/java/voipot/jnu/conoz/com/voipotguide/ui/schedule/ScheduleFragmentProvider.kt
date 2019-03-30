package voipot.jnu.conoz.com.voipotguide.ui.schedule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScheduleFragmentProvider{
    @ContributesAndroidInjector(modules = [ScheduleModule::class])
    abstract fun bindLoginFragment(): ScheduleFragment
}