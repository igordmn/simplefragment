package me.tatarka.simplefragment.test;

import android.os.Parcelable;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.tatarka.simplefragment.BuildConfig;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentStateManager;
import me.tatarka.simplefragment.activity.SimpleFragmentActivity;
import me.tatarka.simplefragment.key.LayoutKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by evan on 3/21/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SimpleFragmentManagerTest {
    private static final LayoutKey KEY = LayoutKey.of(0);

    @Mock
    SimpleFragmentActivity activity;
    @Mock
    ViewGroup rootView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(rootView.findViewById(anyInt())).thenReturn(rootView);
    }

    @Test
    public void testAddAfterSetRootView() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddBeforeSetRootView() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        fragment.wasOnCreateViewCalled = false;
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager = new SimpleFragmentStateManager(activity);
        manager = new SimpleFragmentManager(stateManager, null);
        stateManager.restoreState(stateManagerState);
        manager.restoreState(managerState);

        assertThat(manager.find(KEY)).isEqualTo(fragment);
    }

    @Test
    public void testRemove() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.remove(fragment);

        assertThat(stateManager.getFragments()).isEmpty();
    }

    @Test
    public void testPush() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushAfterConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        ;
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager.clearConfigurationState();
        manager.clearView();
        stateManager = new SimpleFragmentStateManager(activity);
        stateManager.restoreState(stateManagerState);
        manager = new SimpleFragmentManager(stateManager, null);
        manager.restoreState(managerState);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isEqualTo(fragment2);
    }

    @Test
    public void testPop() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.pop();

        assertThat(manager.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);
        manager.pop();

        assertThat(manager.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager.clearConfigurationState();
        manager.clearView();
        stateManager = new SimpleFragmentStateManager(activity);
        stateManager.restoreState(stateManagerState);
        manager = new SimpleFragmentManager(stateManager, null);
        manager.restoreState(managerState);
        manager.setView(rootView);
        manager.pop();

        assertThat(manager.find(KEY)).isEqualTo(fragment1);
    }

    @Test
    public void testGetActivity() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(fragment.getActivity()).isSameAs(activity);
    }

    @Test
    public void testParentFragment() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = fragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(nestedFragment.getParentFragment()).isSameAs(fragment);
    }

    @Test
    public void testRemoveWithNested() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment1 = fragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment2 = nestedFragment1.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.remove(fragment);

        assertThat(stateManager.getFragments()).isEmpty();
        assertThat(fragment.wasOnDestroyCalled && fragment.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedFragment1.wasOnDestroyCalled && nestedFragment1.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedFragment2.wasOnDestroyCalled && nestedFragment2.wasOnViewDestroyedCalled).isTrue();
    }

    @Test
    public void testRemoveDetachedWithNested() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = fragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment pushedFragment = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.remove(fragment);

        assertThat(stateManager.getFragments()).hasSize(1);
        assertThat(stateManager.getFragments().get(0)).isEqualTo(pushedFragment);
        assertThat(fragment.wasOnDestroyCalled && fragment.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedFragment.wasOnDestroyCalled && nestedFragment.wasOnViewDestroyedCalled).isTrue();
    }

    @Test
    public void testPopWithNested() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment pushedFragment = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = pushedFragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.pop();

        assertThat(stateManager.getFragments()).hasSize(1);
        assertThat(stateManager.getFragments().get(0)).isEqualTo(fragment);
        assertThat(pushedFragment.wasOnDestroyCalled && pushedFragment.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedFragment.wasOnDestroyCalled && nestedFragment.wasOnViewDestroyedCalled).isTrue();
    }

    @Test
    public void testRemoveWithNestedBackstack() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = fragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedPushedFragment = fragment.getSimpleFragmentManager().push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.remove(fragment);

        assertThat(stateManager.getFragments()).isEmpty();
        assertThat(fragment.wasOnDestroyCalled && fragment.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedFragment.wasOnDestroyCalled && nestedFragment.wasOnViewDestroyedCalled).isTrue();
        assertThat(nestedPushedFragment.wasOnDestroyCalled && nestedPushedFragment.wasOnViewDestroyedCalled).isTrue();
    }
}
