package com.example.starca.fragments

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.os.Parcelable
import android.provider.BaseColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.starca.R
import com.example.starca.adapters.ListingsAdapter
import com.example.starca.models.Listing
import com.parse.ParseQuery
import kotlinx.parcelize.Parcelize

@Parcelize
class DashboardFragment : Fragment(), Parcelable {


    private val DASHBOARD_KEY = "DASH_DATA"

    lateinit var rvListings: RecyclerView
    lateinit var adapter: ListingsAdapter
    lateinit var searchView: SearchView

    var storageLocations = mutableListOf<StorageLocation>()
    var feedListings: MutableList<Listing> = mutableListOf()
    var storageAddresses: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        searchView = view.findViewById(R.id.search_bar)

        rvListings = view.findViewById(R.id.rvListings) as RecyclerView
        adapter = ListingsAdapter(requireContext(), feedListings)
        rvListings.adapter = adapter
        rvListings.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()

        return view
    }


    @Parcelize
    class DashboardData(
        private val list: List<StorageLocation>,
        public val parent: DashboardFragment
    ) : Parcelable {
        fun storageLocations(): List<StorageLocation> {
            return list
        }
    }

    @Parcelize
    class StorageLocation(val name: String?, val address: String) : Parcelable

    fun selectMarker(index: Int?) {
        if (index != null) {
//            Toast.makeText(context, "clicked $name :: index: $index", Toast.LENGTH_SHORT).show()
            rvListings.smoothScrollToPosition(index)
        } else {
            Log.e(TAG, "selectMarker: Selected Marker without index")
        }
    }

    private fun queryPosts() {
        val query: ParseQuery<Listing> = ParseQuery.getQuery(Listing::class.java)

//        query.include(Listing.KEY_USER)
//        query.addDescendingOrder("createdAt")

        query.limit = 10

        feedListings.clear()

        query.findInBackground { posts, e ->
            // don't convert this to lambda. i like to know what's being called.
            if (e != null) {
                Log.e(TAG, "Error fetching posts: ${e.message}")
            } else {
                if (posts != null) {
                    for (post in posts) {

                        fun getAddressName_Full(): String =
                            "${post.getAddressStreet()}, ${post.getAddressCity()}, ${post.getAddressState()} ${post.getAddressZip()}"

                        val addressName: String? = post.getTitle()

                        storageLocations.add(StorageLocation(addressName, getAddressName_Full()))
                        storageAddresses.add(getAddressName_Full())
                    }
                    val fragmentManager: FragmentManager = childFragmentManager

                    val mapsFrag = MapsFragment()
                    val bundle = Bundle()
                    val parcel = DashboardData(storageLocations, this)

                    bundle.putParcelable(DASHBOARD_KEY, parcel)
                    mapsFrag.arguments = bundle
                    fragmentManager.beginTransaction().replace(R.id.maps_container, mapsFrag)
                        .commit()

                    feedListings.addAll(posts)

                    adapter.notifyDataSetChanged()

                    setupSearch()
                }
            }
        }
    }


    private fun setupSearch(){

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(
            context,
            R.layout.search_item,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        val suggestions = storageAddresses.toList()
        searchView.suggestionsAdapter = cursorAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                Toast.makeText(context, "$query", Toast.LENGTH_SHORT).show()

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                Toast.makeText(context, "$newText", Toast.LENGTH_SHORT).show()

                val cursor =
                    MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

                newText?.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {

                hideKeyboard()

                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor

                val index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                val safeIndex = if (index == -1) 0 else index // cursor.getString requires positive indexes only.

                val selection = cursor.getString(safeIndex)
                searchView.setQuery(selection, false)

                val ii = storageAddresses.indexOf(selection)

                selectMarker(ii)

                return true
            }
        })
    }


    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    companion object {
        const val TAG = "DashboardFragment"
    }

}