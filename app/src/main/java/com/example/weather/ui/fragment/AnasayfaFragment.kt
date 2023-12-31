package com.example.weather.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.MainActivity

import com.example.weather.R
import com.example.weather.data.model.Konum

import com.example.weather.databinding.FragmentAnasayfaBinding
import com.example.weather.ui.adapter.SaatlikAdapter
import com.example.weather.ui.viewmodel.AnasayfaViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AnasayfaFragment : Fragment() {
    private lateinit var binding: FragmentAnasayfaBinding
    private lateinit var viewModel: AnasayfaViewModel
    private var izinKontrol = 0
    private lateinit var flpc: FusedLocationProviderClient
    private lateinit var locationTask: Task<Location>
    var konum = Konum(40.616667,43.1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAnasayfaBinding.inflate(inflater, container, false)

        binding.nextBtn.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.gecis)
        }

        viewModel.anlıkListesi.observe(viewLifecycleOwner){
            binding.textViewBuyukDerece.text = "${it.main.temp}°"
            binding.textViewEnUst.text = it.weather[0].description.toUpperCase()

            when (it.weather[0].main) {
                "Rain" -> binding.imageView.setImageResource(R.drawable.rainy)
                "Clear" -> binding.imageView.setImageResource(R.drawable.sunny)
                "Clouds" -> binding.imageView.setImageResource(R.drawable.cloudy_sunny)
                "Snow" -> binding.imageView.setImageResource(R.drawable.snowy)
                "Drizzle" -> binding.imageView.setImageResource(R.drawable.rainy)
                "Thunderstorm" -> binding.imageView.setImageResource(R.drawable.rainy)
                "Mist" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Smoke" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Haze" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Dust" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Fog" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Sand" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Dust" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Ash" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Squall" -> binding.imageView.setImageResource(R.drawable.cloudy)
                "Tornado" -> binding.imageView.setImageResource(R.drawable.cloudy)
                else -> {
                    Log.e("hata","bilinmeyen hava durumu switch")
                }
            }

            binding.textViewSemsiyeYagmur.text = "%22"

            binding.textViewRuzgarHiziDeger.text = it.wind?.speed.toString()

            binding.textViewNemDeger.text = "%${it.main?.humidity}"

            binding.textViewYuksekMinSicaklik.text = "H:${it.main.temp_max}  L:${it.main.temp_min}"

        }
        binding.rvAnasayfa.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        viewModel.saatListesi.observe(viewLifecycleOwner){
            val saatlikAdapter = SaatlikAdapter(requireContext(),it)
            binding.rvAnasayfa.adapter = saatlikAdapter
        }

        // Şu anki tarih ve saat bilgisini al
        val currentDate = Date()

        // Tarih ve saat bilgisini belirli bir formatta formatla
        val dateFormat = SimpleDateFormat("EEE MMM dd | hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        binding.textViewTarihSaat.text = formattedDate

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: AnasayfaViewModel by viewModels()
        viewModel = tempViewModel

        flpc = LocationServices.getFusedLocationProviderClient(requireActivity())


        izinKontrol = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (izinKontrol != PackageManager.PERMISSION_GRANTED){//izin onaylanmamışsa
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
        }else{//İzin onaylanmışsa
            locationTask = flpc.lastLocation
            konumBilgisiAl()
        }



    }

    fun konumBilgisiAl(){
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                konum.lat = location.latitude
                konum.lon = location.longitude
                viewModel.anasayfaRecyclerview(konum)
                viewModel.anasayfaCurrent(konum)
            } else {
                Log.e("hata","enlem alınamadı")
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100){

            izinKontrol = ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)

            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(),"İzin kabul edildi", Toast.LENGTH_SHORT).show()
                locationTask = flpc.lastLocation
                konumBilgisiAl()
            }else{
                Toast.makeText(requireContext(),"İzin reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }


}