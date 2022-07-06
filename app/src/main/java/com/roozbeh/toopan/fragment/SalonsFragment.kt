package com.roozbeh.toopan.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.roozbeh.toopan.R
import com.roozbeh.toopan.acrivites.AddOrEditSalonActivity
import com.roozbeh.toopan.adapter.SalonsAdapter
import com.roozbeh.toopan.communication.netDetector.NetDetector
import com.roozbeh.toopan.communication.volleyPackage.VolleyController
import com.roozbeh.toopan.communication.volleyRequest.VolleyGetSalons
import com.roozbeh.toopan.databinding.FragmentSalonsBinding
import com.roozbeh.toopan.interfaces.VolleyInterface
import com.roozbeh.toopan.modelApi.PaginationBody
import com.roozbeh.toopan.modelApi.Salons
import com.roozbeh.toopan.modelApi.SalonsResponse
import com.roozbeh.toopan.utility.Constants
import com.roozbeh.toopan.utility.EndlessRecyclerOnScrollListener
import com.roozbeh.toopan.utility.Utils
import com.simform.refresh.SSPullToRefreshLayout

class SalonsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSalonsBinding
    private var manager: LinearLayoutManager? = null
    private var salonsAdapter: SalonsAdapter? = null
    private var salons = arrayListOf<Salons>()
    private val getSalonsTag: String = "getSalonsTag"
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalonsBinding.inflate(layoutInflater)
        binding.fabAddNewSalons.setOnClickListener(this)
        binding.constMakeNewSalon.setOnClickListener(this)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        checkNet(page)
        binding.refreshSalons.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                page = 0
                checkNet(page)
            }

        })

        // Kotlin
        binding.recyclerSalonsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fabAddNewSalons.visibility == View.VISIBLE) {
                    binding.fabAddNewSalons.hide()
                } else if (dy < 0 && binding.fabAddNewSalons.visibility != View.VISIBLE) {
                    binding.fabAddNewSalons.show()
                }
            }
        })

    }

    private fun initRecycler() {
        page = 0
        manager = LinearLayoutManager(requireContext())
        binding.recyclerSalonsList.layoutManager = manager
//        salonsAdapter = salons?.let {
//            SalonsAdapter()
//        }
//        binding.recyclerSalonsList.adapter = salonsAdapter

    }


    private fun checkNet(page: Int) {
        NetDetector.check {
            if (it) {
                requestServers(page)
            } else {
                binding.refreshSalons.setRefreshing(false)
                Utils.showSnackBar(
                    requireContext(),
                    binding.constRecycler,
                    getString(R.string.noInternet),
                    requireContext().getColor(R.color.snackBar)
                )
            }
        }
    }

    private fun requestServers(page: Int) {
        val body = PaginationBody()
        body.page = page
        body.size = 15
        VolleyGetSalons.getSalons(object : VolleyInterface<SalonsResponse> {
            override fun onSuccess(body: SalonsResponse) {
                if (body.salonResponses != null) {
                    if (body.totalElement == 0) {
                        binding.constNullSalons.visibility = View.VISIBLE
                        binding.constRecycler.visibility = View.INVISIBLE
                    } else {
                        binding.constRecycler.visibility = View.VISIBLE
                        binding.constNullSalons.visibility = View.INVISIBLE
                        salons = ArrayList(body.salonResponses!!)
                        showItemsList(salons)
                    }
                }
                binding.refreshSalons.setRefreshing(false)
            }

            override fun onFailed(error: VolleyError?) {
                binding.refreshSalons.setRefreshing(false)
                if (error != null) {
                    error.message?.let { it1 ->
                        Utils.showSnackBar(
                            requireContext(),
                            binding.constRecycler,
                            it1,
                            requireContext().getColor(R.color.snackBar)
                        )
                    }
                }
            }

        }, requireContext(), body, getSalonsTag)
    }

    private fun showItemsList(salons: ArrayList<Salons>) {

        if (salonsAdapter == null || page == 0) {
            salonsAdapter =
                SalonsAdapter(salons, requireContext(), object : SalonsAdapter.OnItemClickListener {
                    override fun onItemClick() {

                    }

                    override fun onEditSalon(salons: Salons) {
                        makeNewSalon(salons)
                    }


                    override fun onManageClick() {
                        manageSalon()
                    }

                })
            binding.recyclerSalonsList.adapter = salonsAdapter
            binding.recyclerSalonsList.addOnScrollListener(object :
                EndlessRecyclerOnScrollListener(manager) {
                override fun onLoadMore(current_page: Int) {
                    page = current_page
                    checkNet(page)
                }
            })
        } else {
            salonsAdapter?.addAll(salons)
        }
    }

    private fun manageSalon() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.fabAddNewSalons.id -> makeNewSalon()

            binding.constMakeNewSalon.id -> makeNewSalon()
        }
    }

    private fun makeNewSalon(salons: Salons? = null) {
        if (salons != null) {
            //edit salon

            val mBundle = Bundle()
            mBundle.putSerializable("salons", salons)
            transactionAddOrEdit(mBundle)
        } else {
            //make new salon
            transactionAddOrEdit()

        }

    }


    private fun transactionAddOrEdit(bundle: Bundle? = null) {
        val intent = Intent(requireContext(), AddOrEditSalonActivity::class.java)
        if (bundle != null) {
            intent.putExtra(Constants.BUNDLE_ADD_OR_EDIT_KEY, bundle)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )


    }

    override fun onStop() {
        super.onStop()
        VolleyController.INSTANCE.getRequestQueue(requireContext()).cancelAll(getSalonsTag)
    }


}