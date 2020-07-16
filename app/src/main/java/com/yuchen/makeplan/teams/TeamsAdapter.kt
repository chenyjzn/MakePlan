package com.yuchen.makeplan.teams

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.Team
import com.yuchen.makeplan.databinding.ItemTeamBinding

class TeamsAdapter(private val viewModel : TeamsViewModel) : RecyclerView.Adapter<TeamsAdapter.TeamHolder>() {

    private var teams: List<Team>? = null

    class TeamHolder(var binding: ItemTeamBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team,viewModel : TeamsViewModel) {
            binding.team = team
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
        return TeamHolder(ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TeamHolder, position: Int) {
        teams?.let {
            holder.bind(it[position],viewModel)
        }
    }

    override fun getItemCount(): Int {
        return teams?.let {it.size} ?: 0
    }

    fun submitTeams(teams: List<Team>){
        this.teams = teams
        notifyDataSetChanged()
    }

//    override fun getFilter(): Filter {
//        return myFilter?:MyFilter()
//    }
//
//    inner class MyFilter() : Filter(){
//        val isInSearch : Boolean = false
//        override fun performFiltering(constraint: CharSequence?): FilterResults {
//            if (isInSearch){
//                val filterTeam = teams?.filter { team ->
//                    constraint?.let {text ->
//                        return@filter (text in team.name)||(text in team.leader.email)||(text in team.leader.displayName)
//                    }
//                    return@filter false
//                }
//                Log.d("chenyjzn","filter team = $filterTeam")
//                val result = FilterResults()
//                result.values = filterTeam
//                return result
//            }else{
//                val filterTeam = teams?.filter { team ->
//                    constraint?.let {text ->
//                        return@filter (text in team.name)||(text in team.leader.email)||(text in team.leader.displayName)
//                    }
//                    return@filter false
//                }
//                Log.d("chenyjzn","filter team = $filterTeam")
//                val result = FilterResults()
//                result.values = filterTeam
//                return result
//            }
//        }
//        @Suppress("UNCHECKED_CAST")
//        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//            results?.let {result ->
//                result.values?.let {list ->
//                    showTeams = list as List<Team>
//                }
//            }
//        }
//    }
}