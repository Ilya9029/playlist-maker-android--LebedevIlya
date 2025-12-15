package com.example.new_project.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.new_project.ui.search.SearchScreen
import com.example.new_project.ui.search.SearchViewModel

class MainActivity : ComponentActivity() {

    private val searchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent(searchViewModel)
        }
    }
}

@Composable
fun MainContent(viewModel: SearchViewModel) {
    Surface {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SearchScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel
            )
        }
    }
}
