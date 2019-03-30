package voipot.jnu.conoz.com.voipotguide.ui.login

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import voipot.jnu.conoz.com.voipotguide.ui.ViewModelKey

@Module
abstract class LoginModule{
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(loginViewModel: LoginViewModel): ViewModel
}