package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.InputField
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {

                    MainContent()
                }
        }
    }
}

@Composable
fun MyApp(content:@Composable () -> Unit){
    MyApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }

}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){



    BillForm(){
        println(it.toInt()*100)
    }

}

@Composable
fun TopHeader(totalPerPerson :Double =124.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .height(150.dp)
        .clip(
            shape = RoundedCornerShape(corner = CornerSize(12.dp)),

            ),
        color = Color(0xffe9d7f7)


    ) {
        Column(modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total="%.2f".format(totalPerPerson)
            Text(text = "Total per person", style = MaterialTheme.typography.h5)
            Text(text = "$$total" ,style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.ExtraBold)

        }

    }
}




@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {


        MainContent()
    }
}


@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier, onValueChange: (String) -> Unit ={}){
    val totalBillState=remember{
        mutableStateOf("")
    }

    val validState= remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController=LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage=(sliderPositionState.value*100).toInt()

    val splitByValue = remember {
        mutableStateOf(1)
    }

    val tipAmountState= remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState= remember {
        mutableStateOf(0.0)
    }
     val range=IntRange(start = 1, endInclusive = 100)

//



    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp,Color.LightGray)
    ) {

        Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {

            TopHeader(totalPerPerson = totalPerPersonState.value)
            InputField(valueState =totalBillState,
                labelId ="Enter Bill" ,
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())

                    keyboardController?.hide()

                }
            )
            
            if(validState){
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Split", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp), horizontalArrangement = Arrangement.End) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByValue.value=
                                    if(splitByValue.value > 1) splitByValue.value-1 else 1
                                totalPerPersonState.value=
                                    caclculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),tipPercentage=tipPercentage, splitBy = splitByValue.value)

                            })
                        
                        Text(text = "${splitByValue.value}", modifier = Modifier
                            .padding(start = 9.dp, end = 9.dp)
                            .align(alignment = Alignment.CenterVertically))

                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitByValue.value < range.last){
                                    splitByValue.value =splitByValue.value+1

                                }
                                totalPerPersonState.value=
                                    caclculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),tipPercentage=tipPercentage, splitBy = splitByValue.value)
                            })


                        
                    }
                }
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(text = "tip", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "$${tipAmountState.value}", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                }
            
            Column(
            verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))
                
                Slider(
                    modifier=Modifier.padding(horizontal = 16.dp),
                    steps=5,

                    value = sliderPositionState.value,
                    onValueChange ={ newVal ->
                    sliderPositionState.value=newVal
                        tipAmountState.value=
                            calculateTotal(totalBillState.value.toDouble(),tipPercentage)


                        totalPerPersonState.value=
                            caclculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),tipPercentage=tipPercentage, splitBy = splitByValue.value)
                } )
            }
            
            }
            else{
                Box() {}
            }


        }

    }
}

fun calculateTotal(totalBill: Double, tipPercentage: Int): Double {
return if(totalBill > 1 && totalBill.toString().isNotEmpty()){
    (totalBill*tipPercentage) /100

}else{
    0.00
}
}

fun caclculateTotalPerPerson(totalBill: Double,tipPercentage: Int,splitBy:Int):Double{
       val bill = calculateTotal(totalBill = totalBill, tipPercentage = tipPercentage)+totalBill
    return (bill/splitBy)
}
