package com.mustafaderinoz.ticketapp.viewmodel

// ana sayfada neler olacağını düşünmeliyiz.
// ui da kesinlikle dto ile çalışmıyoruz
//dto ile çalışırsak değişiklliklere adapte olmak zorundayız
// core ile çalışırsak data sadece bu değişikliklere adapte olmak zorunda
// state demek immutable(değiştirilemez) demek
// state={isloading=false,events=[]} ise state isloading=true diyemeyiz state in kopyası üzerinde deneme yaparız
// asıl state ile yaparsak ui bozulabilir eğer sorun olmazsa copy olan asıl state yerine geçer
//  _state.update { it.copy(isEventsLoading = true, eventsError = null) } bu yüzden state update edilirken state kopyala bunları değiş diyebiliyoruz events=emptyList() de yapabilirdik değiştirmek istediğimz şeyler için güzel bir yöntemdir





import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafaderinoz.core.domain.event.Event
import com.mustafaderinoz.core.domain.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isEventsLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val eventsError: String? = null
)

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        if (_state.value.isEventsLoading) return

        _state.update { it.copy(isEventsLoading = true, eventsError = null) }

        viewModelScope.launch {
            eventRepository.getEvents().fold(
                onSuccess = {
                        list -> _state.update { it.copy(events = list, isEventsLoading = false, eventsError = null)}
                },
                onFailure = {
                        e -> _state.update { it.copy(isEventsLoading = false, eventsError = e.message ?: "Etkinlikler yüklenemedi.") }
                }
            )
        }
    }
}