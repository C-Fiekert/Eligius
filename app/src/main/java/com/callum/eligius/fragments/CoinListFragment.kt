package com.callum.eligius.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.R
import com.callum.eligius.adapters.CoinListener
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.adapters.PortfolioCoinAdapter
import com.callum.eligius.databinding.FragmentCoinListBinding
import com.callum.eligius.helpers.SwipeDelete
import com.callum.eligius.helpers.SwipeEdit
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel
import com.callum.eligius.models.ResponseModel
import com.callum.eligius.models.ResponseModelFactory
import com.callum.eligius.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import retrofit2.Response
import timber.log.Timber
import java.io.File

class CoinListFragment : Fragment(), CoinListener {

    lateinit var app: Main
    private var _fragBinding: FragmentCoinListBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coinList: PortfolioModel? = null
    private val myAdapter by lazy { PortfolioCoinAdapter(coinList?.coins as ArrayList<CoinModel>, null, false, this@CoinListFragment) }
    private lateinit var responseModel: ResponseModel


    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main

        arguments?.let {
            coinList = it.getParcelable("coinlist")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCoinListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Coins"
        var portfolioName = coinList?.name
        fragBinding.textView3.text = portfolioName + "'s Portfolio"

        prepRecycler()

        val repository = Repository()
        val responseModelFactory = ResponseModelFactory(repository)
        responseModel = ViewModelProvider(this, responseModelFactory).get(ResponseModel::class.java)
        responseModel.getBitcoin("bitcoin,ethereum,binancecoin,cardano,solana,ripple,terra-luna,dogecoin,polkadot,avalanche-2", "eur")
        responseModel.myResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                println(response.body().toString())
                response.body()?.let { myAdapter.getPrices(it) }
            } else {
                Log.d("Main", response.errorBody().toString())
                Log.d("Main", response.code().toString())
                println(response.code().toString())
                println("Failed")
            }
        })


        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
        var profileImage = fragBinding.smallProfileImage

        if (user != null) {
            db.child("users").child(user.uid).child("id").get().addOnSuccessListener {

                reference = storage.getReference("userImages/" + user.uid + ".jpg")
                var localfile: File = File.createTempFile("tempfile", ".jpg")
                reference.getFile(localfile).addOnSuccessListener {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    profileImage.setImageBitmap(bitmap)
                    profileImage.maxHeight = 300
                }.addOnFailureListener {
                    println("No profile picture")
                }
            }
        }

        profileImage.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addCoin.setOnClickListener {
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            coinList?.let { it1 -> AddCoinFragment.newInstance(it1, null) }?.let { it2 ->
                transaction.replace(R.id.fragment,
                    it2
                )
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.back.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.favouritesList.setOnClickListener {
            if (fragBinding.favouritesList.isChecked) {
                fragBinding.recyclerView3.adapter = null
                fragBinding.recyclerView3.adapter = coinList?.let {
                    PortfolioCoinAdapter(it.coins as ArrayList<CoinModel>, null, true, this@CoinListFragment)
                }
            } else {
                fragBinding.recyclerView3.adapter = null
                fragBinding.recyclerView3.adapter = coinList?.let {
                    PortfolioCoinAdapter(it.coins as ArrayList<CoinModel>, null, false, this@CoinListFragment)
                }
            }
        }


        fragBinding.portfolioSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("Text provided")
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fragBinding.recyclerView3.adapter = null
                fragBinding.recyclerView3.adapter = coinList?.let {
                    PortfolioCoinAdapter(it.coins as ArrayList<CoinModel>, text, false, this@CoinListFragment)
                }
                return true
            }
        })

        // Swipe to Edit
        val swipeEditHandler = object : SwipeEdit(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                println(viewHolder.adapterPosition)
                coinList?.coins?.get(viewHolder.adapterPosition)?.let { onCoinClick(it) }
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView3)

        // Swipe to Delete
        val swipeDeleteHandler = object : SwipeDelete(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = fragBinding.recyclerView3.adapter as PortfolioCoinAdapter
                println(viewHolder.adapterPosition)

                coinList?.coins?.get(viewHolder.adapterPosition)?.let {
                    db.child("users").child(user.uid).child("portfolios").child(coinList?.id.toString()).child("coins").child(it.id).removeValue().addOnSuccessListener {
                        Timber.i("Deleted coin successfully")
                    }.addOnFailureListener {
                        Timber.i("Unable to delete coin")
                    }
                }

                coinList?.coins?.get(viewHolder.adapterPosition)?.let {
                    db.child("portfolios").child(coinList?.id.toString()).child("coins").child(it.id).removeValue().addOnSuccessListener {
                        Timber.i("Deleted coin successfully")
                    }.addOnFailureListener {
                        Timber.i("Unable to delete coin")
                    }
                }
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView3)

        return root;
    }

    private fun prepRecycler() {
        fragBinding.recyclerView3.adapter = myAdapter
        fragBinding.recyclerView3.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(portfolio: PortfolioModel) =
            CoinListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("coinlist", portfolio)
                }
            }
    }

    override fun onCoinClick(coin: CoinModel) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        coinList?.let { AddCoinFragment.newInstance(it, coin) }?.let {
            transaction.replace(R.id.fragment,
                it
            )
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

}