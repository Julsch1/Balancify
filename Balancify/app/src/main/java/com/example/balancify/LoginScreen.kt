package com.example.balancify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.balancify.components.CheckBoxComponent
import com.example.balancify.components.DividerTextComponent
import com.example.balancify.components.GoToRegisterButton
import com.example.balancify.components.GoogleLoginButton
import com.example.balancify.components.HeadingTextComponent
import com.example.balancify.components.InputTextField
import com.example.balancify.components.LoginButtonComponent
import com.example.balancify.components.NormalTextComponent
import com.example.balancify.components.PasswordTextField

@Composable
fun LoginScreen(navController: NavController) {
    Surface(
        color = colorResource(id = R.color.main_blue),
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.main_blue))
            .padding(28.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            NormalTextComponent(value = stringResource(id = R.string.login))
            HeadingTextComponent(value = stringResource(id = R.string.welcome_back))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person Icon",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            var email = InputTextField(
                labelValue = stringResource(id = R.string.email),
                Icons.Default.Email
            )
            var password = PasswordTextField(
                labelValue = stringResource(id = R.string.password),
                Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(30.dp))

            LoginButtonComponent(email = email, password = password, navController = navController, LocalContext.current)

            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            Spacer(modifier = Modifier.height(20.dp))

            GoogleLoginButton(navController = navController)

            Spacer(modifier = Modifier.height(40.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            GoToRegisterButton(navController)
            Text(text = stringResource(id = R.string.forgot_pwd), color = Color.Black, fontSize = 12.sp)
        }
    }
}

@Preview
@Composable
fun DefaultPreviewLoginScreen(){
    val navController = rememberNavController()
    LoginScreen(navController)
}