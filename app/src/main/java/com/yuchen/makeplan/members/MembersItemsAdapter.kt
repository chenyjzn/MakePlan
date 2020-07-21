package com.yuchen.makeplan.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.databinding.ItemUserBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate

class MembersItemsAdapter() : RecyclerView.Adapter<MembersItemsAdapter.UserHolder>() {

    private var memberList: List<User>? = null

    private var onSelectListener: OnSelectListener? = null
    interface OnSelectListener{
        fun userSelect(user: User)
    }
    fun setOnSelectListener(onSelectListener: OnSelectListener){
        this.onSelectListener = onSelectListener
    }

    inner class UserHolder(var binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.itemUserLayout.setOnClickListener {
                onSelectListener?.userSelect(user)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
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