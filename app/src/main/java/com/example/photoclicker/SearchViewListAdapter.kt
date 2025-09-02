package com.example.photoclicker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photoclicker.Room.Sessions
import com.example.photoclicker.databinding.SearchRvListBinding

class SearchViewListAdapter(private val list: List<Sessions>): RecyclerView.Adapter<SearchViewListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SearchRvListBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SearchRvListBinding = SearchRvListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list.get(position)
            holder.binding.sessionIdTextField.text = item.Sessionid
        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root.context,ActivityViewSessionsPage::class.java)
                .putExtra(
                    "SessionId",
                item.Sessionid
            )
            holder.binding.root.context.startActivity(intent)

        }
    }
}