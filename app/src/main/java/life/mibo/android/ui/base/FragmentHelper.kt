package life.mibo.android.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

public class FragmentHelper(activity: FragmentActivity, layout: Int, fragmentManager: FragmentManager) {
    private var activity: FragmentActivity = activity
    private var fragmentManager: FragmentManager = fragmentManager
    internal var layoutId = layout

    fun replaceFragment(newFragment: Fragment) {
        replaceFragment(newFragment, true, false)
    }

    fun replaceFragmentDontAddToBackstack(newFragment: Fragment) {
        replaceFragment(newFragment, false, false)
    }

    fun replaceFragmentAndClearBackstack(newFragment: Fragment) {
        replaceFragment(newFragment, false, true)
    }

    fun navigateUp() {

        // Some navigateUp calls can be "lost" if they happen after the state has been saved
        if (fragmentManager.isStateSaved) {
            return
        }

        val currentFragment = getCurrentFragment()

        if (fragmentManager.backStackEntryCount > 0) {

            // In a normal world, just popping back stack would be sufficient, but since android
            // is not normal, a call to popBackStack can leave the popped fragment on screen.
            // Therefore, we start with manual removal of the current fragment.
            // Description of the issue can be found here: https://stackoverflow.com/q/45278497/2463035
            removeCurrentFragment()

            if (fragmentManager.popBackStackImmediate()) {
                return  // navigated "up" in fragments back-stack
            }
        }

        //        if (HierarchicalFragment.class.isInstance(currentFragment)) {
        //            Fragment parentFragment =
        //                    ((HierarchicalFragment)currentFragment).getHierarchicalParentFragment();
        //            if (parentFragment != null) {
        //                replaceFragment(parentFragment, false, true);
        //                return; // navigate "up" to hierarchical parent fragment
        //            }
        //        }

        if (activity.onNavigateUp()) {
            return  // navigated "up" to hierarchical parent activity
        }

        activity.onBackPressed() // no "up" navigation targets - just treat UP as back press
    }

    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(getLayoutId())
    }

    private fun replaceFragment(
        newFragment: Fragment,
        addToBackStack: Boolean,
        clearBackStack: Boolean
    ) {
        if (clearBackStack) {
            if (fragmentManager.isStateSaved) {
                // If the state is saved we can't clear the back stack. Simply not doing this, but
                // still replacing fragment is a bad idea. Therefore we abort the entire operation.
                return
            }
            // Remove all entries from back stack
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        val ft = fragmentManager.beginTransaction()

        if (addToBackStack) {
            ft.addToBackStack(null)
        }

        // Change to a new fragment
        ft.replace(getLayoutId(), newFragment, null)

        if (fragmentManager.isStateSaved) {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            ft.commitAllowingStateLoss()
        } else {
            ft.commit()
        }
    }

    private fun removeCurrentFragment() {
        val ft = fragmentManager.beginTransaction()
        ft.remove(getCurrentFragment()!!)
        ft.commit()

        // not sure it is needed; will keep it as a reminder to myself if there will be problems
        // fragmentManager.executePendingTransactions();
    }

    private fun getLayoutId(): Int {
        return layoutId
    }
}