package com.example.projekatfaza23.UI.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.projekatfaza23.UI.navigation.Screen
import com.example.projekatfaza23.model.LoginViewModel
import com.example.projekatfaza23.data.auth.GoogleAuth
import com.example.projekatfaza23.model.LoginUIState
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun LoginScreen(viewModel : LoginViewModel = viewModel(), onLoginSuccess : (Screen) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LoginContent(
        uiState = uiState,
        onLoginClick = {
            viewModel.loginWithGoogle(context, onLoginSuccess)
        }
    )
}


@Composable
fun LoginContent(uiState: LoginUIState, onLoginClick : () -> Unit){

    Column(modifier = Modifier.fillMaxSize()) {
            TopAppBarSection()
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            //TopAppBarSection()
            Surface(
                color = Color.LightGray,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(120.dp)
            ) {
                //should be logo here
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.hrapp_logo),
                        contentDescription = "App Logo ",
                        modifier = Modifier.fillMaxSize().scale(1.8f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Welcome to HR App!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(10.dp))
            Text("Log in", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(48.dp))

            SignIn(
                uiState = uiState,
                onSignInWithGoogleClick = onLoginClick
            )


            Text(
                text = "By continuing, you agree to Terms of Service and acknowledge our Privacy Policy.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 10.dp).padding(horizontal = 28.dp),
                color = Color.Gray
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

}


@Composable
fun SignIn(
    uiState: LoginUIState,
    onSignInWithGoogleClick: () -> Unit
){
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)){

        Spacer(modifier = Modifier.height(24.dp))

        //google sign in
        Button(
            onClick = {
                onSignInWithGoogleClick()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF116379), contentColor = Color.White),
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
                Text("Continue with Google", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }

    }
}

@Preview(showBackground =  true)
@Composable
fun LoginScreenPreview(){
    LoginContent (LoginUIState(),  {})
}