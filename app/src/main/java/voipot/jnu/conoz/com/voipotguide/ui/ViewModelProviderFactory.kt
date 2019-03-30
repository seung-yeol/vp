package voipot.jnu.conoz.com.voipotguide.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

//google IOsched에서 가져옴
class ViewModelProviderFactory @Inject constructor(
        //여기서의 Map은 module들에 @IntoMap을 달고 있는 VewModel 메서드에 의해 제공됨.
        //kotlin map은 Map<K, out V>라서 자바상에서는 creator가 Map<Class<? extends ViewModel>,? extends Provider<ViewModel>>로 바뀌어서
        //우리가 제공해줄 데이터는 Map<Class<out ViewModel>, Provider<ViewModel>>라서(대거가 자바로 만들어져서) JvmSuppressWildcards를 사용하여 저기 out을 무시해줌
        private val creators: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val found = creators.entries.find { modelClass.isAssignableFrom(it.key) }
        val creator = found?.value
                ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
