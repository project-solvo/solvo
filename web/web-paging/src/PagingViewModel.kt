import androidx.compose.runtime.*
import org.solvo.web.viewModel.AbstractViewModel

@Stable
open class PagingViewModel : AbstractViewModel() {
    fun clickPrePage(list: List<Any>) {
        if (_currentPage.value != 0) {
            _currentPage.value = _currentPage.value - 1
            _currentAnswer1.value = list[_currentPage.value * 2]
            _currentAnswer2.value = list[_currentPage.value * 2 + 1]
        }
    }

    fun clickNextPage(list: List<Any>) {
        answerSize.value = list.size
        if (_currentPage.value < determineMaxPage()) {
            _currentPage.value = _currentPage.value + 1
            _currentAnswer1.value = list[_currentPage.value * 2]
            _currentAnswer2.value = list[_currentPage.value * 2 + 1]
        }
    }

    fun determineMaxPage(): Int {
        return if (answerSize.value == 0) {
            0
        } else if (answerSize.value % 2 == 0) {
            answerSize.value / 2 - 1
        } else {
            answerSize.value / 2
        }
    }

    private val answerSize: MutableState<Int> = mutableStateOf(0)

    private val _currentPage: MutableState<Int> = mutableStateOf(0)
    val currentPage: State<Int> get() = _currentPage

    private val _currentAnswer1: MutableState<Any?> = mutableStateOf(null)
    val currentAnswer1: State<Any?> get() = _currentAnswer1

    private val _currentAnswer2: MutableState<Any?> = mutableStateOf(null)
    val currentAnswer2: State<Any?> get() = _currentAnswer2

}