package com.example.projekatfaza23.UI.login

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.R
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.model.LoginViewModel
import com.example.projekatfaza23.data.auth.GoogleAuth
import com.example.projekatfaza23.model.LoginUIState
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun LoginScreen(viewModel : LoginViewModel, navigateHome : () -> Unit){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TopAppBarSection()
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
        Text("Log in", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        SignIn(
            uiState = uiState,
            onSignInWithGoogleClick = {
                viewModel.loginWithGoogle(
                    activityContext = context,
                    navigateHome = navigateHome
                )
            }
        )

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

}

@Composable
fun SignIn(
    uiState: LoginUIState,
    onSignInWithGoogleClick: () -> Unit
){
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)){

        Spacer(modifier = Modifier.height(24.dp))

        //google sign in
        OutlinedButton(
            onClick = {
                onSignInWithGoogleClick()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Gray
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Google", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "By continuing, you agree to Terms of Service and acknowledge our Privacy Policy.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Preview(showBackground =  true)
@Composable
fun LoginScreenPreview(){
    //ovdje ce sad biti bug!!!
    LoginScreen(viewModel(), {})
}