package com.example.projekatfaza23

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.model.LoginViewModel

@Composable
fun LoginScreen(viewModel : LoginViewModel){
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){

        TopAppBarSection({})
        Spacer(modifier = Modifier.height(60.dp))

        Surface(color = Color.LightGray,
               shape = RoundedCornerShape(12.dp),
               modifier = Modifier.size(120.dp)){
            //should be logo here
            Box(contentAlignment = Alignment.Center){
                Text("HR", fontSize = 22.sp, fontWeight = FontWeight.Black, color= Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Welcome to HR App!", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(40.dp))
        Text("Log in or create account", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        EmailAndPassword(uiState.email, uiState.password, {viewModel.updateEmail(it)}, {viewModel.updatePassword(it)}, {viewModel.login()}, uiState.isLoading)

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (uiState.isLoginSuccessful) {
            // Ovdje bi išao kod za prelazak na drugi ekran
            // npr. LaunchedEffect(Unit) { navController.navigate("home") }
            Text("Uspješno ste prijavljeni!", color = Color(0xFF4CAF50))
        }
    }

}

@Composable
fun EmailAndPassword(email: String, password: String, updateEmail: (String) -> Unit, updatePassword: (String) -> Unit, login: () -> Unit, isLoading: Boolean){
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)){
        Text("Email Address", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = email,
            onValueChange = {updateEmail(it)},
            modifier = Modifier.fillMaxWidth(),
            placeholder = {Text("Enter your email")}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = password,
            onValueChange = {updatePassword(it)},
            modifier = Modifier.fillMaxWidth(),
            placeholder = {Text("Enter your password")}
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { login() },
            modifier = Modifier.fillMaxWidth(),
            enabled =  !isLoading,
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ){
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Log in")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically){
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "By continuing, you agree to Terms of Service and acknowledge our Privacy Policy.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        //google sign in
        OutlinedButton(
            onClick = {/* to do */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continue with Google", color = Color.Black)
        }

    }
}

@Preview(showBackground =  true)
@Composable
fun LoginScreenPreview(){
    LoginScreen(viewModel())
}