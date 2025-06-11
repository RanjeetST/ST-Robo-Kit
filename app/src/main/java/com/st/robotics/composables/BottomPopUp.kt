import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor

@Composable
fun MessageDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Alert",
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text(text = "OK", color = OnPrimary)
            }
        }
    )
}