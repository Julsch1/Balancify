package com.example.balancify
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.balancify.components.CheckBoxComponent
import com.example.balancify.components.DividerTextComponent
import com.example.balancify.components.GoogleLoginButton
import com.example.balancify.components.HeadingTextComponent
import com.example.balancify.components.InputTextField
import com.example.balancify.components.LoginButtonComponent
import com.example.balancify.components.NormalTextComponent
import com.example.balancify.components.PasswordTextField
import com.example.balancify.components.RegisterButtonComponent
import com.example.balancify.components.ToLoginButtonComponent

//package com.example.balancify

@Composable
fun RegisterScreen(navController: NavController){
    val context = LocalContext.current

    Surface (
        color = colorResource(id = R.color.main_blue),
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.main_blue))
            .padding(28.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))

            var first_name =  InputTextField(
                labelValue = stringResource(id = R.string.first_name),
                Icons.Default.Person
            )

            var last_name = InputTextField(
                labelValue = stringResource(id = R.string.last_name),
                Icons.Default.Person
            )
            var email = InputTextField(
                labelValue = stringResource(id = R.string.email),
                Icons.Default.Email
            )
            var password = PasswordTextField(
                labelValue = stringResource(id = R.string.password),
                Icons.Default.Lock
            )

            CheckBoxComponent(value = stringResource(id = R.string.terms_conditions))
            Spacer(modifier = Modifier.height(30.dp))

            RegisterButtonComponent(first_name = first_name, last_name = last_name, email = email, password = password, navController, context)

            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            Spacer(modifier = Modifier.height(20.dp))

            Column (horizontalAlignment = Alignment.Start){
                GoogleLoginButton(navController = navController)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = stringResource(id = R.string.already_registered), color = Color.Black)
            Column (horizontalAlignment = Alignment.Start){
                ToLoginButtonComponent(name = "Julia", navController = navController)
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreviewRegisterScreen(){
    val navController = rememberNavController()
    RegisterScreen(navController)
}
