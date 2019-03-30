package voipot.jnu.conoz.com.voipotguide.ui.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import voipot.jnu.conoz.com.voipotguide.ui.ViewModelKey

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedPagerViewModel::class)
    abstract fun bindPagerViewModel(sharedPagerViewModel: SharedPagerViewModel): ViewModel
}