package com.example.birds_of_a_feather_team_20;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.birds_of_a_feather_team_20.model.db.SessionDatabase;
import com.example.birds_of_a_feather_team_20.sorting.ProfileComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * This class holds the list of profiles for the current session.
 */
public class ProfilesCollection {
    private final List<Profile> profiles;
//    private final List<MatchProfilePair> matchPairs;
//    ProfilesViewAdapter adapter;
    private static ProfilesCollection singletonInstance;

    public static boolean databaseDebug = false;

    private final Stack<Integer> additions;
    private final Stack<Integer> modifications;
    private final Stack<Pair<Integer, Integer>> movements;
    private final HashMap<Profile, Integer> oldPositions;

    private ProfileComparator comparator;

    public static ProfilesCollection singleton() {
        if (singletonInstance == null) {
            singletonInstance = new ProfilesCollection();
        }
        return singletonInstance;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
    public Stack<Integer> getAdditions() {
        return additions;
    }
    public Stack<Integer> getModifications() {
        return modifications;
    }
    public Stack<Pair<Integer, Integer>> getMovements() { return movements; }

    public ProfilesCollection(/*ProfilesViewAdapter adapter*/) {
        profiles = new ArrayList<>();
//        matchPairs = new ArrayList<>();
        additions = new Stack<>();
        modifications = new Stack<>();
        movements = new Stack<>();
        oldPositions = new HashMap<>();
    }

    /**
     * Inserts a new profile or updates an existing one depending on whether a profile is found
     * with its id
     * @param profile
     */
    public void addOrUpdateProfile(Profile profile, Context context) {

        if (profile == null) return;

//        if (courseMatches == 0) {
//            return; // Don't add to list if no matches
//        } // Not handled here anymore

        int index = getProfiles().indexOf(profile);
        if (index == -1) {
            insertNewProfile(profile, getProfiles().size());
//            applySort();
        }
        else {
            updateExistingProfile(profile, index);
        }

//        String sessionName = MainActivity.sessionName;
//        if (context != null && SessionDatabase.singleton(context) != null
//                && SessionDatabase.singleton(context).sessionDao() != null && sessionName != null
//                && !databaseDebug)
//            SessionDatabase.singleton(context).sessionDao().insertProfile(sessionName, profile);
        updateProfileInDB(profile, context);
        applySort();
    }

    /**
     * Inserts the changes into the database of sessions
     */
    public void updateProfileInDB(Profile profile, Context context) {
        List<Profile> profiles = getProfiles();
        for(int i = 0; i < profiles.size(); i++)
        {
            if (profiles.get(i).getId().equals(profile.getId())) {

                if(profile.getIsFavorite()) {
                    profiles.get(i).setFavorite();
                } else{
                    profiles.get(i).unFavorite();
                }
            }
        }

        String sessionName = MainActivity.sessionName;
        if (context != null && SessionDatabase.singleton(context) != null
                && SessionDatabase.singleton(context).sessionDao() != null)
        {/*sessionName != null*/
            if (profile.getSessionId() == -1) {
                if (sessionName != null) {

                    SessionDatabase.singleton(context).sessionDao().insertProfile(sessionName, profile);
                }
            }
            else {

                SessionDatabase.singleton(context).sessionDao().insertProfileViaId(profile.getSessionId(), profile);
            }
        }
    }


    /**
     * Updates a profile that already exists in the collection with new data
     */
    private void updateExistingProfile(Profile newProfile, int index) {
//        Utilities.logToast(activity, "Update existing profile: " + newProfile.getName());

        Profile existing = getProfiles().get(index);
        if(newProfile == null || existing == null) {
            return; // Note: Making your profile null will not remove it from others' lists
        }
        if (existing.strongEquals(newProfile)) return; // Don't update if they already match (would unnecessarily update the list view)
        existing.updateProfile(newProfile);
        getModifications().add(index);
    }

    /**
     * Insert a profile not already in the collection
     */
    private void insertNewProfile(Profile profile, int index) {
//        Utilities.logToast(activity, "Adding to List: " + profile.serialize());
        getAdditions().add(index);
        getProfiles().add(index, profile); // no profile with this id, so add it
        // Note, not sure if it is necessary to tell the adapter that all the other items moved down
    }

    /**
     * Sets a new comparator for sorting
     * @param comparator
     */
    public void changeSort(ProfileComparator comparator) {
        this.comparator = comparator;
        applySort();
    }

    /**
     * Sorts the list according to the currently set comparator
     */
    private void applySort() {
//        recordMovementsStart();
        getProfiles().sort(comparator);
//        recordMovementsStop();
        Log.d("Sorted Profiles", "-------");
        for(Profile profile : getProfiles()) {
            Log.d("Sorted Profile", profile.getName());
        }
        /// RECORD MOVEMENTS
    }

//    private void recordMovementsStart() {
//        oldPositions.clear();
//        List<Profile> profileList = getProfiles();
//        for (int i = 0; i < profileList.size(); i++) {
//            Profile p = profileList.get(i);
//            oldPositions.put(p, i);
//        }
//    }
//    private void recordMovementsStop() {
//        List<Profile> profileList = getProfiles();
//        for (int i = 0; i < profileList.size(); i++) {
//            Profile profile = profileList.get(i);
////            oldPositions.put(p, i);
//            int oldPos = oldPositions.get(profile);
//
//            if (i != oldPos) {
//                getMovements().add(new Pair(oldPos, i));
//            }
//        }
//    }
}
