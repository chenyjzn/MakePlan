package com.yuchen.makeplan.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemAvatarBinding

class AvatarAdapter() : RecyclerView.Adapter<AvatarAdapter.AvatarHolder>() {

    private var memberList: List<User>? = null

    class AvatarHolder(var binding: ItemAvatarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
        return AvatarHolder(ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
        memberList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return memberList?.let {it.size} ?: 0
    }

    fun submitMembers(memberList: List<User>) {
        this.memberList = memberList
        notifyDataSetChanged()
    }
}