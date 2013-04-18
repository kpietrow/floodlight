package net.floodlightcontroller.cli.commands;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.jboss.netty.channel.Channel;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFType;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.protocol.statistics.OFStatistics;
import org.restlet.resource.ClientResource;

import jline.console.completer.Completer;
import net.floodlightcontroller.cli.IConsole;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IFloodlightProviderService.Role;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.Controller;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.switchoverflow.WebResources;
import net.floodlightcontroller.threadpool.IThreadPoolService;

/**
 * This command will return the current topology of the
 * network when called.
 * 
 * @author Kevin Pietrow, <kpietrow@gmail.com>
 */
public class AddFlowCmd implements ICommand, IConsole, IFloodlightModule,
IOFMessageListener, IOFSwitch{
	/** The command string. */
	private String commandString = "add flow";
	/** The command's arguments. */
	private String arguments = null;
	/** The command's help text. */
	private String help = "Add a new flow to current network";
	/** The prompt string of the command line. */
    private String prompt;
	// These use the Rest API
	protected static IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;
	protected static IStaticFlowEntryPusherService sfp;

	@Override
	public String getCommandString() {
		return commandString;
	}
	
	@Override
	public String getArguments() {
		return arguments;
	}

	@Override
	public String getHelpText() {
		return help;
	}
	
	
	@Override
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Override
	public String getPrompt() {
		if (this.prompt != null)
			return this.prompt;
		
		return "> ";
	}

	@Override
	public Collection<Completer> getCompleters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(String string) throws IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = 
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IRestApiService.class);
        return l;
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		sfp = context.getServiceImpl(IStaticFlowEntryPusherService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}
	
	
	 /**
     * used to push given route using static flow entry pusher
     * @param boolean inBound
     * @param Route route
     * @param IPClient client
     * @param LBMember member
     * @param long pinSwitch
     */
	@Override
	public String execute(IConsole console, String arguments) {
		

		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

		/*
		try {
			Map<Long, IOFSwitch> activeswitches = floodlightProvider.getSwitches();
		} catch (NullPointerException e){
			return "activeswitches nogo";
		}
		finally {
			return "activeswitches ok";
		}
    	String currentswitch = (activeswitches.get(new Long(1)).toString());
    	int index = currentswitch.indexOf("DPID");
    	String dpid = currentswitch.substring(index + 5, index + 28);
		
		*/
		String dpid = "00:00:00:00:00:00:00:01";
		
		
		OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
		OFMatch ofMatch = new OFMatch();
		fm.setMatch(ofMatch);

		String id = String.valueOf((int) 5);

		sfp.addFlow(id, fm, dpid);
		
		
		
		String temp = "everybody poops";
		return temp;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFloodlightProvider(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThreadPoolService(IThreadPoolService threadPool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChannel(Channel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(OFMessage m, FloodlightContext bc) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(List<OFMessage> msglist, FloodlightContext bc)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnectOutputStream() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBuffers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getActions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCapabilities() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getTables() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFeaturesReply(OFFeaturesReply featuresReply) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<OFPhysicalPort> getEnabledPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Short> getEnabledPortNumbers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFPhysicalPort getPort(short portNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFPhysicalPort getPort(String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPort(OFPhysicalPort port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePort(short portNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePort(String portName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<OFPhysicalPort> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean portEnabled(short portName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean portEnabled(String portName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean portEnabled(OFPhysicalPort port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStringId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress getInetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Object, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getConnectedSince() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNextTransactionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Future<List<OFStatistics>> getStatistics(OFStatisticsRequest request)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<OFFeaturesReply> querySwitchFeaturesReply()
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deliverOFFeaturesReply(OFMessage reply) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelFeaturesReply(int transactionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConnected(boolean connected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Role getHARole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHARole(Role role, boolean haRoleReplyReceived) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliverStatisticsReply(OFMessage reply) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelStatisticsReply(int transactionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelAllStatisticsReplies() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean attributeEquals(String name, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object removeAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllFlowMods() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean updateBroadcastCache(Long entry, Short port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<Short, Long> getPortBroadcastHits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendStatsQuery(OFStatisticsRequest request, int xid,
			IOFMessageListener caller) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Lock getListenerReadLock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Lock getListenerWriteLock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSwitchProperties(OFDescriptionStatistics description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OFPortType getPortType(short port_num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFastPort(short port_num) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Short> getUplinkPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}


}
