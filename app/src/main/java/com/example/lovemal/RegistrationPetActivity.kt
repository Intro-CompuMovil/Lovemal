package com.example.lovemal

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider
import java.text.DecimalFormat

class RegistrationPetActivity : AppCompatActivity() {

    private var isDogSelecter:Boolean = true
    private var isDog2Selecter:Boolean = false
    private var alturaActual:Int = 30
    private var pesoBase: Int = 1

    private lateinit var dog1:CardView
    private lateinit var dog2:CardView
    private lateinit var txtAge: TextView
    private lateinit var rsAge: RangeSlider
    private lateinit var btnMasAltura: FloatingActionButton
    private lateinit var btnMenosAltura:FloatingActionButton
    private lateinit var txtAltura: TextView
    private lateinit var btnMasPeso: FloatingActionButton
    private lateinit var btnMenosPeso:FloatingActionButton
    private lateinit var txtPeso: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_pet)

        initComponent()
        initListeners()
        initUI()

        val btnRegistrar = findViewById<Button>(R.id.idBtnRegistrar)
        btnRegistrar.setOnClickListener { Toast.makeText(this, "¡Mascota registrada!", Toast.LENGTH_SHORT).show() }
    }

    private fun initComponent(){
        dog1 = findViewById(R.id.Dog1)
        dog2 = findViewById(R.id.Dog2)
        txtAge = findViewById(R.id.txtAge)
        rsAge = findViewById(R.id.rsAge)
        btnMasAltura = findViewById(R.id.btnMasAltura)
        btnMenosAltura = findViewById(R.id.btnMenosAltura)
        txtAltura = findViewById(R.id.txtAltura)
        btnMasPeso = findViewById(R.id.btnMasPeso)
        btnMenosPeso = findViewById(R.id.btnMenosPeso)
        txtPeso = findViewById(R.id.txtPeso)

    }
    private fun initListeners(){
        dog1.setOnClickListener {
            changeDog()
            setDogColor()
        }
        dog2.setOnClickListener {
            changeDog()
            setDogColor()
        }
        rsAge.addOnChangeListener { _, fl, _ ->
            val df = DecimalFormat("#.##")
            val result = df.format(fl)
            txtAge.text = "$result meses"

        }
        btnMasAltura.setOnClickListener{
            alturaActual += 1
            setAltura()
            txtAltura.text = "$alturaActual cm"
        }
        btnMenosAltura.setOnClickListener {
            alturaActual-= 1
            setAltura()
            txtAltura.text = "$alturaActual cm"
        }
        btnMenosPeso.setOnClickListener {
            pesoBase-= 1
            setPeso()
            txtPeso.text = "$pesoBase Kg"
        }
        btnMasPeso.setOnClickListener {
            pesoBase+= 1
            setPeso()
            txtPeso.text = "$pesoBase Kg"
        }

    }
    private fun setPeso(){
        txtPeso.text = pesoBase.toString()
    }

    private fun setAltura(){
        txtAltura.text = alturaActual.toString()
    }
    private fun setDogColor(){
        dog1.setCardBackgroundColor(getBackgroundColor(isDogSelecter))
        dog2.setCardBackgroundColor(getBackgroundColor(isDog2Selecter))

    }
    private fun changeDog(){
        isDogSelecter = !isDogSelecter
        isDog2Selecter =!isDog2Selecter
    }
    private fun getBackgroundColor(isSelectedComponent:Boolean):Int{
        val colorReference = if(isSelectedComponent){
            R.color.fondoSeleccionado
        }else{
            R.color.fondo
        }
        return ContextCompat.getColor(this,colorReference)
    }
    private fun initUI(){
        setDogColor()
        setAltura()
        setPeso()
    }
}
