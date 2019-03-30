package voipot.jnu.conoz.com.voipotguide.dagger.module

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import voipot.jnu.conoz.com.voipotguide.ui.ViewModelProviderFactory
import voipot.jnu.conoz.com.voipotguide.util.DataManager
import voipot.jnu.conoz.com.voipotguide.util.IDataManager

@Module
abstract class AppModule {
    @Binds
    abstract fun bindDataManager(dataManager: DataManager): IDataManager

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}