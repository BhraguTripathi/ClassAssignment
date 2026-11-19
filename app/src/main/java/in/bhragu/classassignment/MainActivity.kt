package `in`.bhragu.classassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import `in`.bhragu.classassignment.data.AppDatabase
import `in`.bhragu.classassignment.data.ContactRepository
import `in`.bhragu.classassignment.ui.ContactScreen
import `in`.bhragu.classassignment.ui.ContactViewModel
import `in`.bhragu.classassignment.ui.theme.ClassAssignmentTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ContactRepository(database.contactDao())
        ContactViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClassAssignmentTheme {
                ContactScreen(viewModel = viewModel)
            }
        }
    }
}
