package com.technoholicdeveloper.kwizzapp.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.technoholicdeveloper.kwizzapp.MainActivity

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.helper.Constants
import com.technoholicdeveloper.kwizzapp.login.LoginFragment
import com.technoholicdeveloper.kwizzapp.play.PlayMenuFragment
import com.technoholicdeveloper.kwizzapp.wallet.WalletFragment
import net.rmitsolutions.mfexpert.lms.helpers.clearPrefs
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import org.jetbrains.anko.find
import kotlin.math.sign

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DashboardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DashboardFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var leftToRight: Animation
    private lateinit var rightToLeft : Animation
    private lateinit var buttonPlay : Button
    private lateinit var buttonAchievements : Button
    private lateinit var buttonLeaderboards : Button
    private lateinit var buttonWallet : Button
    private lateinit var buttonSignOut : Button

    private val CLICKABLES = intArrayOf(R.id.buttonPlay, R.id.buttonAchievements, R.id.buttonLeaderboards, R.id.buttonWallet, R.id.buttonSignOut)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        buttonPlay = view.find(R.id.buttonPlay)
        buttonAchievements = view.find(R.id.buttonAchievements)
        buttonLeaderboards = view.find(R.id.buttonLeaderboards)
        buttonWallet = view.find(R.id.buttonWallet)
        buttonSignOut = view.find(R.id.buttonSignOut)
        rightToLeft = AnimationUtils.loadAnimation(activity, R.anim.right_to_left)
        leftToRight = AnimationUtils.loadAnimation(activity, R.anim.left_to_right)
        buttonPlay.animation = rightToLeft
        buttonAchievements.animation = leftToRight
        buttonLeaderboards.animation = rightToLeft
        buttonWallet.animation = leftToRight
        buttonSignOut.animation = rightToLeft
        for (id in CLICKABLES){
            view.find<Button>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonSignOut ->{
                signOut()
            }
            R.id.buttonAchievements ->{
                Games.getAchievementsClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
                        .achievementsIntent
                        .addOnSuccessListener { intent -> startActivityForResult(intent, Constants.RC_ACHIEVEMENT_UI) }
            }
            R.id.buttonLeaderboards ->{
                Games.getLeaderboardsClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
                        .getLeaderboardIntent(getString(R.string.leaderboard_global_rank))
                        .addOnSuccessListener { intent -> startActivityForResult(intent, Constants.RC_LEADERBOARD_UI) }
            }

            R.id.buttonWallet ->{
                val walletFragment = WalletFragment()
                switchToFragment(activity!!, walletFragment)
            }

            R.id.buttonPlay ->{
                val playMenuFragment = PlayMenuFragment()
                switchToFragment(activity!!,playMenuFragment)
            }
        }
    }

    private fun signOut() {
        if (isSignedIn()) {
            val signInClient = GoogleSignIn.getClient(activity!!,
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            signInClient.signOut().addOnCompleteListener(activity!!
            ) {
                // at this point, the user is signed out.
                logD("Sign Out Successfully !")
                activity?.clearPrefs()
                logD("Shared Preferences deleted successfully")
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        } else {
            logD( "Already sign out !")
        }
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(activity) != null
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DashboardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}