package voipot.jnu.conoz.com.voipotguide.ui.schedule

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import voipot.jnu.conoz.com.voipotguide.ui.ViewModelKey
import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.ScheduleAdapterViewModel

@Module
abstract class ScheduleModule {
    @Binds
    @IntoMap
    @ViewModelKey(ScheduleViewModel::class)
    abstract fun bindScheduleViewModel(scheduleViewModel: ScheduleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleAdapterViewModel::class)
    abstract fun bindScheduleAdapterViewModel(scheduleAdapterViewModel: ScheduleAdapterViewModel): ViewModel
}