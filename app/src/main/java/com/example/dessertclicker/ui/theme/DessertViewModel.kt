package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.DessertUiState
import com.example.dessertclicker.data.Datasource.dessertList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {

    private val _dessertUiState = MutableStateFlow(DessertUiState())
    val dessertUiState: StateFlow<DessertUiState> = _dessertUiState.asStateFlow()

    // Track the current dessert index for each dessert type
    private var dessert1Index = 0
    private var dessert2Index = 0

    // List of desserts for each type
    private val dessert1List = dessertList.take(dessertList.size / 2)
    private val dessert2List = dessertList.drop(dessertList.size / 2)

    init {
        // Initialize with the first desserts from each list
        _dessertUiState.value = DessertUiState(
            dessert1ImageId = dessert1List[0].imageId,
            dessert2ImageId = dessert2List[0].imageId,
            dessert1Price = dessert1List[0].price,
            dessert2Price = dessert2List[0].price
        )
    }

    fun onDessert1Clicked() {
        _dessertUiState.update { currentState ->
            val newDessert1Sold = currentState.dessert1Sold + 1
            val newDessert1Revenue = newDessert1Sold * currentState.dessert1Price
            val newTotalRevenue = newDessert1Revenue + currentState.dessert2Revenue
            val newTotalDessertsSold = newDessert1Sold + currentState.dessert2Sold

            // Determine if we should show the next dessert
            val nextDessert1Index = determineNextDessertIndex(dessert1Index, newDessert1Sold, dessert1List)

            // Update the current dessert index if changed
            if (nextDessert1Index != dessert1Index) {
                dessert1Index = nextDessert1Index
            }

            currentState.copy(
                dessert1Sold = newDessert1Sold,
                dessert1Revenue = newDessert1Revenue,
                totalRevenue = newTotalRevenue,
                totalDessertsSold = newTotalDessertsSold,
                dessert1ImageId = dessert1List[dessert1Index].imageId,
                dessert1Price = dessert1List[dessert1Index].price
            )
        }
    }

    fun onDessert2Clicked() {
        _dessertUiState.update { currentState ->
            val newDessert2Sold = currentState.dessert2Sold + 1
            val newDessert2Revenue = newDessert2Sold * currentState.dessert2Price
            val newTotalRevenue = newDessert2Revenue + currentState.dessert1Revenue
            val newTotalDessertsSold = newDessert2Sold + currentState.dessert1Sold

            // Determine if we should show the next dessert
            val nextDessert2Index = determineNextDessertIndex(dessert2Index, newDessert2Sold, dessert2List)

            // Update the current dessert index if changed
            if (nextDessert2Index != dessert2Index) {
                dessert2Index = nextDessert2Index
            }

            currentState.copy(
                dessert2Sold = newDessert2Sold,
                dessert2Revenue = newDessert2Revenue,
                totalRevenue = newTotalRevenue,
                totalDessertsSold = newTotalDessertsSold,
                dessert2ImageId = dessert2List[dessert2Index].imageId,
                dessert2Price = dessert2List[dessert2Index].price
            )
        }
    }

    /**
     * Determine which dessert to show based on the number of desserts sold
     */
    private fun determineNextDessertIndex(
        currentIndex: Int,
        dessertsSold: Int,
        dessertList: List<com.example.dessertclicker.model.Dessert>
    ): Int {
        var newIndex = currentIndex
        for (index in dessertList.indices) {
            if (dessertsSold >= dessertList[index].startProductionAmount) {
                newIndex = index
            } else {
                // No need to continue checking, as the startProductionAmounts are sorted
                break
            }
        }
        return newIndex
    }
}